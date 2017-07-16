package com.device.inspect.common.service;

import DNA.Core.Transaction;
import DNA.Core.TransferTransaction;
import DNA.Network.Rest.RestNode;
import DNA.sdk.wallet.UserWalletManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by fgz on 2017/7/13.
 */
public class OnchainService {

    private static final Logger LOGGER = LogManager.getLogger(OnchainService.class);

    private UserWalletManager wallet = InitWallet.getWallet();

    private String registerAddr = "AQrzw7oAzbM9YyskXevu87fG933Tes4efv"; //资产所有人
    private String rewardAddr = "Af4MFkKMVZeJD55M5KXrfw7n1jSwhSEvfv";   //积分所有人
    private String agencyAddr = "ARtfmVhnh39CXFndEgXCcxfADUdWmD8Nv6";  //中间商 账号
    private String updaterAddr = "AVaKEVVeBy5uGkNhCFwxq3iHpyMAm5CD8f"; //更新 状态的账号，改账号已在区块链配置，其他账号无权更新状态
    private String department0Addr = "ANR74azedN2Fmd6qTpYZSfVPthSw3DgZN9"; //部门0  账号
    private String department1Addr = "AZgdpWNg36SDLHb4FubMX5DDUFij6Uf6G6"; //部门1 账号
    private String user0Addr = "ASKpNaaKkPQqHjiF3RJm7BZdDsRdQrbsbs";   //用户1 账号
    private String user1Addr = "AVra1GeYivUUeoT7HKvJzhFhuWwdT2WYz5";   //用户2 账号
    private long assetsIssued = 100000000;  //每次签发的资产

    public boolean transfer(String assetid, long amount, String desc, String formAddr, String toAddr) {
        Transaction tx;
        String txHex;
        boolean success = false;
        //转移
        tx = wallet.createTrfTx(formAddr, assetid, amount, toAddr, desc);
        txHex = wallet.signTx(tx);
        try {
            success = wallet.sendTx(txHex);
            Thread.sleep(7000);
            LOGGER.info("转移tx:" + success + ",txid:" + tx.hash().toString());
            RestNode restNode = new RestNode(InitWallet.url);

            Transaction newtx = restNode.getRawTransaction(tx.hash().toString());
            if (newtx instanceof TransferTransaction) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
