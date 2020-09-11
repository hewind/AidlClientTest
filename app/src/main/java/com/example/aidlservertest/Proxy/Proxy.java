package com.example.aidlservertest.Proxy;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.example.aidlservertest.Bean.PersonBean;
import java.util.List;

/**
 * 日期：2020-09-08 17:22
 * 类说明：Proxy为发送数据，内部类Stub为接收数据
 */
public class Proxy implements PersonManagerInterface {

    //远程binder对象
    private IBinder iBinder;
    //Binder ID
    private static final String DESCRIPTOR = "com.example.aidlservertest.Proxy.PersonManagerInterface";

    public Proxy(IBinder iBinder) {
        this.iBinder = iBinder;
    }

    //增加人数实现
    @Override
    public void addPerson(PersonBean personBean) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel replay = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            if (personBean != null){
                data.writeInt(1);
                personBean.writeToParcel(data,0);
            }else {
                data.writeInt(0);
            }
            iBinder.transact(Stub.TRANSAVTION_addperson,data,replay,0);
            replay.readException();
        }finally {
            replay.recycle();
            data.recycle();
        }
    }
    //删除人数实现
    @Override
    public void deletePerson(PersonBean personBean) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel replay = Parcel.obtain();
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            if (personBean != null){
                data.writeInt(1);
                personBean.writeToParcel(data,0);
            }else {
                data.writeInt(0);
            }
            iBinder.transact(Stub.TRANSAVTION_deleteperson,data,replay,0);
            replay.readException();
        }finally {
            replay.recycle();
            data.recycle();
        }
    }
    //查询人数实现
    @Override
    public List<PersonBean> getPerson() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel replay = Parcel.obtain();
        List<PersonBean> result;
        try {
            //写入一个校验
            data.writeInterfaceToken(DESCRIPTOR);
            //客户端调用到这个方法，走到iBinder.transact，此时会进入系统层处理，处理结果通过调用Stub的transact方法返回，系统层处理过程会阻塞在这行代码，直到处理完成，
            iBinder.transact(Stub.TRANSAVTION_getpersons,data,replay,0);
            //上面一行代码在系统处理成功后，返回结果，由于Stub的transact方法写入了一个【reply.writeNoException()】，这里reply进行读取异常
            replay.readException();
            //这里是Stub的transact方法处理返回的结果
            result = replay.createTypedArrayList(PersonBean.CREATOR);
        }finally {
            replay.recycle();
            data.recycle();
        }
        return result;
    }

    /**
     * 方法说明：返回当前获取到的服务端的IBander
     * 日期：2020-09-09 10:31
     */
    @Override
    public IBinder asBinder() {
        return iBinder;
    }

}
