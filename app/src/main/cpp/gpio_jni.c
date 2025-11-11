#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <linux/gpio.h>
#include <sys/ioctl.h>

#define MAX_LINES 64

// 解析 int 数组
static void parse_lines_from_java(JNIEnv *env, jintArray arr, unsigned int *lines, int *n)
{
    jint *elems = (*env)->GetIntArrayElements(env, arr, NULL);
    *n = (*env)->GetArrayLength(env, arr);
    for (int i = 0; i < *n; i++) lines[i] = elems[i];
    (*env)->ReleaseIntArrayElements(env, arr, elems, 0);
}

// -------- JNI 方法实现 --------

// public static native String list(String chip);
//JNIEXPORT jstring JNICALL Java_com_example_gpio_GpioCtl_list
//        (JNIEnv *env, jclass clazz, jstring jchip)
//{
//    const char *chip = (*env)->GetStringUTFChars(env, jchip, 0);
//    int fd = open(chip, O_RDWR);
//    if(fd<0){
//        (*env)->ReleaseStringUTFChars(env, jchip, chip);
//        return (*env)->NewStringUTF(env, "open failed");
//    }
//
//    struct gpiochip_info cinfo;
//    if(ioctl(fd, GPIO_GET_CHIPINFO_IOCTL, &cinfo)<0){
//        close(fd);
//        (*env)->ReleaseStringUTFChars(env, jchip, chip);
//        return (*env)->NewStringUTF(env, "GPIO_GET_CHIPINFO_IOCTL failed");
//    }
//
//    char buf[4096];
//    int off = snprintf(buf, sizeof(buf), "Chip name: %s, label: %s, lines: %u\n",
//                       cinfo.name, cinfo.label, cinfo.lines);
//
//    for(unsigned int i=0;i<cinfo.lines;i++){
//        struct gpioline_info linfo;
//        memset(&linfo,0,sizeof(linfo));
//        linfo.line_offset=i;
//        if(ioctl(fd, GPIO_GET_LINEINFO_IOCTL, &linfo)==0){
//            off += snprintf(buf+off, sizeof(buf)-off,
//                            " line %3u: name='%s' consumer='%s' flags=0x%x\n",
//                            i, linfo.name, linfo.consumer, linfo.flags);
//        }
//    }
//
//    close(fd);
//    (*env)->ReleaseStringUTFChars(env, jchip, chip);
//    return (*env)->NewStringUTF(env, buf);
//}

JNIEXPORT jstring JNICALL Java_com_example_gpio_GpioCtl_list
        (JNIEnv *env, jclass clazz, jstring jchip)
{
    const char *chip = (*env)->GetStringUTFChars(env, jchip, 0);
    int fd = open(chip, O_RDWR);
    if (fd < 0) {
        (*env)->ReleaseStringUTFChars(env, jchip, chip);
        return (*env)->NewStringUTF(env, "open failed");
    }

    struct gpiochip_info cinfo;
    if (ioctl(fd, GPIO_GET_CHIPINFO_IOCTL, &cinfo) < 0) {
        close(fd);
        (*env)->ReleaseStringUTFChars(env, jchip, chip);
        return (*env)->NewStringUTF(env, "GPIO_GET_CHIPINFO_IOCTL failed");
    }

    // 动态分配 buffer，预估每行最多 128 字节
    size_t bufsize = 256 + cinfo.lines * 128;
    char *buf = (char *)malloc(bufsize);
    if (!buf) {
        close(fd);
        (*env)->ReleaseStringUTFChars(env, jchip, chip);
        return (*env)->NewStringUTF(env, "malloc failed");
    }

    int off = snprintf(buf, bufsize, "Chip name: %s, label: %s, lines: %u\n",
                       cinfo.name, cinfo.label, cinfo.lines);

    for (unsigned int i = 0; i < cinfo.lines; i++) {
        struct gpioline_info linfo;
        memset(&linfo, 0, sizeof(linfo));
        linfo.line_offset = i;
        if (ioctl(fd, GPIO_GET_LINEINFO_IOCTL, &linfo) == 0) {
            int ret = snprintf(buf + off, bufsize - off,
                               " line %3u: name='%s' consumer='%s' flags=0x%x\n",
                               i, linfo.name, linfo.consumer, linfo.flags);
            if (ret < 0) break; // snprintf 出错
            off += ret;
            if (off >= bufsize - 1) break; // 防止越界
        }
    }

    jstring result = (*env)->NewStringUTF(env, buf);
    free(buf);
    close(fd);
    (*env)->ReleaseStringUTFChars(env, jchip, chip);
    return result;
}

// public static native int get(String chip, int line);
JNIEXPORT jint JNICALL Java_com_example_gpio_GpioCtl_get
        (JNIEnv *env, jclass clazz, jstring jchip, jint line)
{
    const char *chip = (*env)->GetStringUTFChars(env, jchip, 0);
    int fd = open(chip,O_RDWR);
    if(fd<0){
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return -1;
    }

    struct gpiohandle_request req;
    memset(&req,0,sizeof(req));
    req.lineoffsets[0] = line;
    req.lines = 1;
    req.flags = GPIOHANDLE_REQUEST_INPUT;
    strcpy(req.consumer_label,"gpioctl");

    if(ioctl(fd, GPIO_GET_LINEHANDLE_IOCTL, &req)<0){
        close(fd);
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return -1;
    }

    struct gpiohandle_data data;
    if(ioctl(req.fd, GPIOHANDLE_GET_LINE_VALUES_IOCTL, &data)<0){
        close(req.fd);
        close(fd);
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return -1;
    }

    close(req.fd);
    close(fd);
    (*env)->ReleaseStringUTFChars(env,jchip,chip);
    return data.values[0];
}

// public static native int set(String chip, int line, int value);
JNIEXPORT jint JNICALL Java_com_example_gpio_GpioCtl_set
        (JNIEnv *env, jclass clazz, jstring jchip, jint line, jint value)
{
    const char *chip = (*env)->GetStringUTFChars(env,jchip,0);
    int fd = open(chip,O_RDWR);
    if(fd<0){
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return -1;
    }

    struct gpiohandle_request req;
    memset(&req,0,sizeof(req));
    req.lineoffsets[0]=line;
    req.lines=1;
    req.flags=GPIOHANDLE_REQUEST_OUTPUT;
    req.default_values[0]=value;
    strcpy(req.consumer_label,"gpioctl");

    if(ioctl(fd, GPIO_GET_LINEHANDLE_IOCTL,&req)<0){
        close(fd);
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return -1;
    }

    close(req.fd);
    close(fd);
    (*env)->ReleaseStringUTFChars(env,jchip,chip);
    return 0;
}

// public static native int[] getMulti(String chip, int[] lines);
JNIEXPORT jintArray JNICALL Java_com_example_gpio_GpioCtl_getMulti
        (JNIEnv *env, jclass clazz, jstring jchip, jintArray jlines)
{
    const char *chip = (*env)->GetStringUTFChars(env,jchip,0);
    unsigned int lines[MAX_LINES];
    int n;
    parse_lines_from_java(env,jlines,lines,&n);

    int fd = open(chip,O_RDWR);
    if(fd<0){(*env)->ReleaseStringUTFChars(env,jchip,chip); return NULL;}

    struct gpiohandle_request req;
    memset(&req,0,sizeof(req));
    for(int i=0;i<n;i++) req.lineoffsets[i]=lines[i];
    req.lines=n;
    req.flags=GPIOHANDLE_REQUEST_INPUT;
    strcpy(req.consumer_label,"gpioctl");

    if(ioctl(fd,GPIO_GET_LINEHANDLE_IOCTL,&req)<0){
        close(fd);
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return NULL;
    }

    struct gpiohandle_data data;
    if(ioctl(req.fd, GPIOHANDLE_GET_LINE_VALUES_IOCTL,&data)<0){
        close(req.fd);
        close(fd);
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return NULL;
    }

    jintArray ret = (*env)->NewIntArray(env,n);
    (*env)->SetIntArrayRegion(env, ret, 0, n, (jint*)data.values);

    close(req.fd);
    close(fd);
    (*env)->ReleaseStringUTFChars(env,jchip,chip);
    return ret;
}

// public static native int setMulti(String chip, int[] lines, int[] values);
JNIEXPORT jint JNICALL Java_com_example_gpio_GpioCtl_setMulti
        (JNIEnv *env, jclass clazz, jstring jchip, jintArray jlines, jintArray jvals)
{
    const char *chip = (*env)->GetStringUTFChars(env,jchip,0);
    unsigned int lines[MAX_LINES];
    int vals[MAX_LINES];
    int nlines,nvals;
    parse_lines_from_java(env,jlines,lines,&nlines);
    parse_lines_from_java(env,jvals,(unsigned int*)vals,&nvals);

    if(nlines!=nvals){
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return -1;
    }

    int fd = open(chip,O_RDWR);
    if(fd<0){(*env)->ReleaseStringUTFChars(env,jchip,chip); return -1;}

    struct gpiohandle_request req;
    memset(&req,0,sizeof(req));
    for(int i=0;i<nlines;i++){
        req.lineoffsets[i]=lines[i];
        req.default_values[i]=vals[i];
    }
    req.lines=nlines;
    req.flags=GPIOHANDLE_REQUEST_OUTPUT;
    strcpy(req.consumer_label,"gpioctl");

    if(ioctl(fd,GPIO_GET_LINEHANDLE_IOCTL,&req)<0){
        close(fd);
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return -1;
    }

    close(req.fd);
    close(fd);
    (*env)->ReleaseStringUTFChars(env,jchip,chip);
    return 0;
}

// public static native int setDir(String chip, int line, boolean input);
JNIEXPORT jint JNICALL Java_com_example_gpio_GpioCtl_setDir
        (JNIEnv *env, jclass clazz, jstring jchip, jint line, jboolean input)
{
    const char *chip = (*env)->GetStringUTFChars(env,jchip,0);
    int fd = open(chip,O_RDWR);
    if(fd<0){(*env)->ReleaseStringUTFChars(env,jchip,chip); return -1;}

    struct gpiohandle_request req;
    memset(&req,0,sizeof(req));
    req.lineoffsets[0]=line;
    req.lines=1;
    if(input) req.flags=GPIOHANDLE_REQUEST_INPUT;
    else { req.flags=GPIOHANDLE_REQUEST_OUTPUT; req.default_values[0]=0; }
    strcpy(req.consumer_label,"gpioctl");

    if(ioctl(fd,GPIO_GET_LINEHANDLE_IOCTL,&req)<0){
        close(fd);
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return -1;
    }

    close(req.fd);
    close(fd);
    (*env)->ReleaseStringUTFChars(env,jchip,chip);
    return 0;
}

// public static native long waitEvent(String chip, int line, String type);
JNIEXPORT jlong JNICALL Java_com_example_gpio_GpioCtl_waitEvent
        (JNIEnv *env, jclass clazz, jstring jchip, jint line, jstring jtype)
{
    const char *chip = (*env)->GetStringUTFChars(env,jchip,0);
    const char *etype = (*env)->GetStringUTFChars(env,jtype,0);

    int fd = open(chip,O_RDWR);
    if(fd<0){(*env)->ReleaseStringUTFChars(env,jchip,chip); return -1;}

    struct gpioevent_request ereq;
    memset(&ereq,0,sizeof(ereq));
    ereq.lineoffset=line;
    strcpy(ereq.consumer_label,"gpioctl");
    if(!strcmp(etype,"rising")) ereq.eventflags=GPIOEVENT_REQUEST_RISING_EDGE;
    else if(!strcmp(etype,"falling")) ereq.eventflags=GPIOEVENT_REQUEST_FALLING_EDGE;
    else ereq.eventflags=GPIOEVENT_REQUEST_BOTH_EDGES;

    if(ioctl(fd,GPIO_GET_LINEEVENT_IOCTL,&ereq)<0){
        close(fd);
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return -1;
    }

    struct gpioevent_data ev;
    if(read(ereq.fd,&ev,sizeof(ev))<=0){
        close(ereq.fd);
        close(fd);
        (*env)->ReleaseStringUTFChars(env,jchip,chip);
        return -1;
    }

    close(ereq.fd);
    close(fd);
    (*env)->ReleaseStringUTFChars(env,jchip,chip);
    return ev.timestamp;
}