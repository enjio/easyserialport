/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gpiotest2;


/**
 * @hide
 */
public class GpioPort {

    private static final String TAG = "GpioPort";

    // used by the JNI code
    private int mNativeContext;
	
	static {
		System.loadLibrary("gpioport");
	}
	
    public boolean gpio_init(){
        boolean ret = false;
        ret = native_gpio_init();
        if(ret == false)
        {
            throw new IllegalArgumentException("open /dev/mtgpio error");
        }
	return ret;
    }

    /**
     * Closes the Gpio port
     */
    public boolean gpio_deinit(){
        boolean ret = false;
        ret = native_gpio_deinit();
        if(ret == false)
        {
            throw new IllegalArgumentException("close /dev/mtgpio error");
        }
		return ret;
    }

    public int get_max_number(){
	return native_gpio_max_number();
    }

    public boolean set_mode(int gpio, int mode){
        boolean ret = false;
        ret = native_gpio_set_mode(gpio, mode);
        if(ret == false)
        {
            throw new IllegalArgumentException("set gpio mode(0/1/2/3) error");
        }
	return ret;
    }

    public boolean set_dir(int gpio, int dir){
        boolean ret = false;
        ret = native_gpio_set_dir(gpio, dir);
        if(ret == false)
        {
            throw new IllegalArgumentException("set gpio dir input/output error");
        }
	return ret;
    }

    public boolean set_data(int gpio, int data){
        boolean ret = false;
        ret = native_gpio_set_data(gpio, data);
        if(ret == false)
        {
            throw new IllegalArgumentException("set gpio value high/low error");
        }
	return ret;
    }

    public boolean set_pullen(int gpio, int pullen){
        boolean ret = false;
        ret = native_gpio_set_pull_en(gpio, pullen);
        if(ret == false)
        {
            throw new IllegalArgumentException("set gpio pull enable error");
        }
	return ret;
    }

    public boolean set_pull(int gpio, int pull){
        boolean ret = false;
        ret = native_gpio_set_pull(gpio, pull);
        if(ret == false)
        {
            throw new IllegalArgumentException("set gpio pull high/low error");
        }
	return ret;
    }

    public int get_data(int gpio){
        int ret = -1;
        ret = native_gpio_get_data(gpio);
        if(ret < 0)
        {
            throw new IllegalArgumentException("get gpio value high/low error");
        }
	return ret;
    }

    public int get_datain(int gpio){
        int ret = -1;
        ret = native_gpio_get_datain(gpio);
        if(ret < 0)
        {
            throw new IllegalArgumentException("get gpio value high/low error");
        }
	return ret;
    }
    
    private native boolean native_gpio_init();
    private native boolean native_gpio_deinit();
    private native int native_gpio_max_number();
    private native boolean native_gpio_set_mode(int gpio_number, int mode);
    private native boolean native_gpio_set_dir(int gpio_number, int dir);
    private native boolean native_gpio_set_data(int gpio_number, int data);
    private native boolean native_gpio_set_pull_en(int gpio_number, int pullen);
    private native boolean native_gpio_set_pull(int gpio_number, int pull);
    private native int native_gpio_get_data(int gpio_number);
    private native int native_gpio_get_datain(int gpio_number);
}
