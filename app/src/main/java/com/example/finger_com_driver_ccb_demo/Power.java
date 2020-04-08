package com.example.finger_com_driver_ccb_demo;

import android.serialport.DeviceControlSpd;

import java.io.IOException;

/**
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                            O\ = /O
 * //                        ____/`---'\____
 * //                      .   ' \\| |// `.
 * //                       / \\||| : |||// \
 * //                     / _||||| -:- |||||- \
 * //                       | | \\\ - /// | |
 * //                     | \_| ''\---/'' | |
 * //                      \ .-\__ `-` ___/-. /
 * //                   ___`. .' /--.--\ `. . __
 * //                ."" '< `.___\_<|>_/___.' >'"".
 * //               | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //                 \ \ `-. \_ __\ /__ _/ .-` / /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //
 * //         .............................................
 * //                  佛祖镇楼                  BUG辟易
 *
 * @author :EchoXBR in  2020/4/7 下午4:59.
 * 功能描述:指纹模块上电工具类
 */
public class Power {
    static Power power;
    static DeviceControlSpd deviceControlSpd;

    public static Power getIntance() {
        if (power == null) {
            power = new Power();
            try {
                deviceControlSpd = new DeviceControlSpd(DeviceControlSpd.POWER_NEWMAIN);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return power;
    }

    public  void powerOn() {
        try {
            // 使用指纹模块需要把76拉低，2.4G 模块需要拉高76
            deviceControlSpd.newSetGpioOff(76);
            deviceControlSpd.newSetGpioOn(75);
            deviceControlSpd.newSetGpioOn(88);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void powerOff() {
        try {
            deviceControlSpd.newSetGpioOff(75);
            deviceControlSpd.newSetGpioOff(88);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
