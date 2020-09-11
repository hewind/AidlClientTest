package com.example.aidlservertest.Proxy;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.aidlservertest.Bean.PersonBean;
import java.util.List;

/**
 * 日期：2020-09-11 14:36
 * 类说明：Sub类继承自binder实现了 PersonManagerService 接口方法，说明它可以跨进程传输，并可进行服务端相关的数据操作
 */
public abstract class Stub extends Binder implements PersonManagerInterface  {

    //Binder ID
    private static final String DESCRIPTOR = "com.example.aidlservertest.Proxy.PersonManagerInterface";

    public Stub() {
        //attachInterface方法：向BinderService注册Binder服务。只有注册了binder，客户端才能查询到有这个binder对象，并使用它
        this.attachInterface(this,DESCRIPTOR);
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    //将服务端的Binder对象准换成客户端所需要的AIDL接口对象，
    public static PersonManagerInterface asInterface(IBinder iBinder){
        if ((iBinder==null)) {
            return null;
        }
        //查询当前进程，如果客户端和服务端位于同一进程，那么此方法返回的就是当前进程的Stub对象本身，否则返回服务端进程的Stub.proxy对象（服务端也有该Stub类）
        IInterface iin = iBinder.queryLocalInterface(DESCRIPTOR);
        if ((iin != null) && (iin instanceof PersonManagerInterface)) {
            return (PersonManagerInterface)iin;
        }
        //返回服务端的Stub.proxy对象
        return new Proxy(iBinder);
    }

    //这个方法运行在服务端中的Binder线程池当中，当客户端发起跨进程请求时，远程请求会通过系统底层封装后交由此方法处理。
    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        switch (code){
            case INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTOR);
                return true;
            case TRANSAVTION_addperson://增加人数
                //返回一个校验
                data.enforceInterface(DESCRIPTOR);
                PersonBean personBean = null;
                if (data.readInt() != 0){
                    //读取PersonBean
                    personBean = PersonBean.CREATOR.createFromParcel(data);
                }
                //接口方法回调出去，这里最终会走到服务端的Service类的IBinder回调中
                this.addPerson(personBean);
                //返回一个无异常
                reply.writeNoException();
                return true;
            case TRANSAVTION_deleteperson://删除人数
                data.enforceInterface(DESCRIPTOR);
                PersonBean personBean2 = null;
                if (data.readInt() != 0){
                    personBean2 = PersonBean.CREATOR.createFromParcel(data);
                }
                this.deletePerson(personBean2);
                reply.writeNoException();
                return true;
            case TRANSAVTION_getpersons://获取人数
                data.enforceInterface(DESCRIPTOR);
                List<PersonBean> result = this.getPerson();
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;
        }
        return super.onTransact(code, data, reply, flags);
    }

    //使用int型代替字符串传递来标识调用的是哪一个方法，属于性能优化
    public static final int TRANSAVTION_getpersons = IBinder.FIRST_CALL_TRANSACTION;
    public static final int TRANSAVTION_addperson = IBinder.FIRST_CALL_TRANSACTION + 1;
    public static final int TRANSAVTION_deleteperson = IBinder.FIRST_CALL_TRANSACTION + 2;

}
