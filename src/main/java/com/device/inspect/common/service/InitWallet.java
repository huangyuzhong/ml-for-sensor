package com.device.inspect.common.service;

import DNA.sdk.info.account.AccountAsset;
import DNA.sdk.wallet.UserWalletManager;

import DNA.Wallets.Contract;

import DNA.sdk.info.account.AccountInfo;

/**
 * Created by fgz on 2017/7/13.
 */
public class InitWallet {

//    public static String url = "http://139.196.115.69:20334";

    public static String url = "http://42.159.233.49:50334";

    // 定义一个私有构造方法
    private InitWallet(){}

    private static volatile UserWalletManager wallet;

    public static UserWalletManager getWallet() {
        if (wallet == null) {
            synchronized (OnchainService.class) {
                if (wallet == null) {
                    String path = "walletDb.dat";
                    String accessToken = "";                // 非必需项，如开启OAuth认证，则需要填写
                    wallet = UserWalletManager.getWallet(path, url, accessToken);
                }
            }
        }
        return wallet;
    }

}
