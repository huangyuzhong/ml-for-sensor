package com.device.inspect.common.service;

import DNA.Core.Transaction;
import DNA.Core.TransferTransaction;
import DNA.Network.Rest.RestNode;
import DNA.sdk.info.account.AccountAsset;
import DNA.sdk.info.account.Asset;
import DNA.sdk.wallet.UserWalletManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.device.inspect.common.service.InitWallet.url;

/**
 * Created by fgz on 2017/7/13.
 */
@Component("OnchainService")
public class OnchainService {

    private static final Logger LOGGER = LogManager.getLogger(OnchainService.class);

    private UserWalletManager wallet;

    public static String registerAddr = "AQrzw7oAzbM9YyskXevu87fG933Tes4efv"; //资产所有人
    public static String rewardAddr = "Af4MFkKMVZeJD55M5KXrfw7n1jSwhSEvfv";   //积分所有人

    public static String AssetId = "c2b15086a51ee3abb28a6cdb6debf42b97cd409625b55c4033b912a575726b7c";
    public static String RewordAssetId = "c87bc5063c7d8fc0366c1410895cd810ab6d37250640fd8882473add55202a6d";

    public OnchainService() {
        wallet = InitWallet.getWallet();
        wallet.startSyncBlock();
    }

    public void SyncBlock() throws Exception {
        while (!wallet.hasFinishedSyncBlock()) {
            System.out.println("blockHeight:" + wallet.blockHeight());
            Thread.sleep(100);
        }
    }
    public String getAssetId(String accountAddress){
        if (accountAddress != null){
            AccountAsset accountAsset = wallet.getAccountAsset(accountAddress);
            List<Asset> assetList = accountAsset.canUseAssets;
            return assetList.get(0).assetid;
        }else{
            return null;
        }
    }

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
            RestNode restNode = new RestNode(url);

            Transaction newtx = restNode.getRawTransaction(tx.hash().toString());
            if (newtx instanceof TransferTransaction) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendStateUpdateTx(String namespace, String key, String addr, String jsonStr) throws Exception {
        addr = InitWallet.updaterAddr;

        //状态更新
        UserWalletManager wallet = InitWallet.getWallet();
        Transaction tx;
        String txHex;
        boolean success = false;
        tx = wallet.createStateUpdateTx(namespace, key, jsonStr, addr);
        txHex = wallet.signTx(tx);
        wallet.sendTx(txHex);
    }
}
