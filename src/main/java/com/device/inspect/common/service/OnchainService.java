package com.device.inspect.common.service;

import DNA.Core.StateUpdateTransaction;
import DNA.Core.Transaction;
import DNA.Core.TransferTransaction;
import DNA.Helper;
import DNA.Network.Rest.RestNode;
import DNA.Wallets.Contract;
import DNA.sdk.helper.OnChainSDKHelper;
import DNA.sdk.info.account.AccountAsset;
import DNA.sdk.info.account.AccountInfo;
import DNA.sdk.wallet.UserWalletManager;
import com.alibaba.fastjson.JSONObject;
import com.device.inspect.common.model.charater.User;
import com.device.inspect.common.repository.charater.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import static com.device.inspect.common.service.InitWallet.getWallet;
import static com.device.inspect.common.service.InitWallet.url;

/**
 * Created by fgz on 2017/7/13.
 */
@Component
public class OnchainService {

    private static final Logger LOGGER = LogManager.getLogger(OnchainService.class);

    private UserWalletManager wallet;

    private String registerAddr = "AQrzw7oAzbM9YyskXevu87fG933Tes4efv"; //资产所有人
    public static String rewardAddr = "Af4MFkKMVZeJD55M5KXrfw7n1jSwhSEvfv";   //积分所有人
    public static String agencyAddr = "ARtfmVhnh39CXFndEgXCcxfADUdWmD8Nv6";  //中间商 账号
    private String updaterAddr = "AVaKEVVeBy5uGkNhCFwxq3iHpyMAm5CD8f"; //更新 状态的账号，改账号已在区块链配置，其他账号无权更新状态
    private String department0Addr = "ANR74azedN2Fmd6qTpYZSfVPthSw3DgZN9"; //部门0  账号
    private String department1Addr = "AZgdpWNg36SDLHb4FubMX5DDUFij6Uf6G6"; //部门1 账号
    private String user0Addr = "ASKpNaaKkPQqHjiF3RJm7BZdDsRdQrbsbs";   //用户1 账号
    private String user1Addr = "AVra1GeYivUUeoT7HKvJzhFhuWwdT2WYz5";   //用户2 账号

    String AssetId = "c2b15086a51ee3abb28a6cdb6debf42b97cd409625b55c4033b912a575726b7c";
    String RewordAssetId = "c87bc5063c7d8fc0366c1410895cd810ab6d37250640fd8882473add55202a6d";

    String agencyPriKey = "23ddf7ba8afbbdcc228e30749d0c8ec0958e8fd882453bad0a9e918d97b6c84d";
    String updaterPriKey = "d46f336479abfef0fa4bcfbaa0268138b39fe5e55fc0f2b529ca2e8eb2ba9d91";
    String rewardSenderPriKey = "03150eca22c2b6fc547012829a35badb576981dd17738695a803bb0c07c4d720";   //积分发放者

    String department0PriKey = "d24c06c385429f2a573d85634539b1b5846f6ce80369aefcb5b4dce0480342ed";
    String department1PriKey = "258473fde94f3f2b426dfe80ad1aafdec75b4f18129393cc3d0fe2a6709f70bb";

    String rewardSenderAddr = "AVgUyufEiq78qrPLfE45uGLqZYEfMxMcgv";   //积分发放者
    private long assetsIssued = 100000000;  //每次签发的资产

    public AccountAsset getAccountAsset(String userAddr){
        return wallet.getAccountAsset(userAddr);
    }

    public OnchainService() {
        wallet = InitWallet.getWallet();
        initAccount();
        wallet.startSyncBlock();
    }

    public void SyncBlock() throws Exception {
        while (!wallet.hasFinishedSyncBlock()) {
            System.out.println("blockHeight:" + wallet.blockHeight());
            Thread.sleep(100);
        }
    }

    public void initAccount(){
        Contract[] contracts = wallet.getWallet().loadContracts();
        if (contracts.length == 1) { //新生成钱包
            wallet.restartSyncBlock();
            rewardSenderAddr = wallet.createAccount(rewardSenderPriKey);
            agencyAddr = wallet.createAccount(agencyPriKey);
            updaterAddr = wallet.createAccount(updaterPriKey);
            department0Addr = wallet.createAccount(department0PriKey);
            department1Addr = wallet.createAccount(department1PriKey);
//            user0Addr = wallet.createAccount(user0PriKey);
//            user1Addr = wallet.createAccount(user1PriKey);
            System.out.println("stateupdater addr:" + updaterAddr);
            contracts = wallet.getWallet().loadContracts();
        }
        for (Contract c : contracts) {
            AccountInfo acct = wallet.getAccountInfo(c.address());
            AccountAsset asset = wallet.getAccountAsset(acct.address);
            //System.out.println(acct.address + "   " + asset.canUseAssets+"  ");

            System.out.println(acct.toString());
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

    public JSONObject sendStateUpdateTx(String namespace, String key, String addr, String jsonStr) throws Exception {
        addr = updaterAddr;

        //状态更新
        UserWalletManager wallet = InitWallet.getWallet();
        Transaction tx;
        String txHex;
        boolean success = false;
        tx = wallet.createStateUpdateTx(namespace, key, jsonStr, addr);
        txHex = wallet.signTx(tx);
        success = wallet.sendTx(txHex);
        //System.out.println("StateUpdateTransaction:" + success + ",txid:" + tx.hash().toString());
        //System.out.println(Helper.toHexString(tx.scripts[0].code));
        Thread.sleep(7000);

        JSONObject strJson = null;
        RestNode restNode = new RestNode(url);
        try {
            Transaction newtx = restNode.getRawTransaction(tx.hash().toString());
            if (newtx instanceof StateUpdateTransaction) {
                StateUpdateTransaction t = (StateUpdateTransaction) newtx;
                System.out.println(new String(t.namespace) + "   " + new String(t.key) + "   " + new String(t.value));
                strJson = JSONObject.parseObject(new String(t.value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strJson;
    }
}
