package com.device.inspect.common.service;

import DNA.sdk.info.account.AccountAsset;
import DNA.sdk.wallet.UserWalletManager;

import DNA.Wallets.Contract;

import DNA.sdk.info.account.AccountInfo;

/**
 * Created by fgz on 2017/7/13.
 */
public class InitWallet {

    public static String url = "http://42.159.233.49:50334";

    public static String agencyAddr = "ARtfmVhnh39CXFndEgXCcxfADUdWmD8Nv6";  //中间商 账号
    public static String updaterAddr = "AVaKEVVeBy5uGkNhCFwxq3iHpyMAm5CD8f"; //更新 状态的账号，改账号已在区块链配置，其他账号无权更新状态
    public static String department0Addr = "ANR74azedN2Fmd6qTpYZSfVPthSw3DgZN9"; //部门0  账号
    public static String department1Addr = "AZgdpWNg36SDLHb4FubMX5DDUFij6Uf6G6"; //部门1 账号

    public static String agencyPriKey = "23ddf7ba8afbbdcc228e30749d0c8ec0958e8fd882453bad0a9e918d97b6c84d";
    public static String updaterPriKey = "d46f336479abfef0fa4bcfbaa0268138b39fe5e55fc0f2b529ca2e8eb2ba9d91";
    public static String rewardSenderPriKey = "03150eca22c2b6fc547012829a35badb576981dd17738695a803bb0c07c4d720";   //积分发放者

    public static String department0PriKey = "d24c06c385429f2a573d85634539b1b5846f6ce80369aefcb5b4dce0480342ed";
    public static String department1PriKey = "258473fde94f3f2b426dfe80ad1aafdec75b4f18129393cc3d0fe2a6709f70bb";

    public static String rewardSenderAddr = "AVgUyufEiq78qrPLfE45uGLqZYEfMxMcgv";   //积分发放者

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
                    initAccount();
                    wallet.startSyncBlock();
                }
            }
        }
        return wallet;
    }

    private static void initAccount(){
        Contract[] contracts = wallet.getWallet().loadContracts();
        if (contracts.length == 1) { //新生成钱包
            wallet.restartSyncBlock();
            rewardSenderAddr = wallet.createAccount(rewardSenderPriKey);
            agencyAddr = wallet.createAccount(agencyPriKey);
            updaterAddr = wallet.createAccount(updaterPriKey);
            department0Addr = wallet.createAccount(department0PriKey);
            department1Addr = wallet.createAccount(department1PriKey);
            System.out.println("stateupdater addr:" + updaterAddr);
            contracts = wallet.getWallet().loadContracts();
        }
        for (Contract c : contracts) {
            AccountInfo acct = wallet.getAccountInfo(c.address());
            AccountAsset asset = wallet.getAccountAsset(acct.address);
            System.out.println(acct.toString());
        }
    }

}
