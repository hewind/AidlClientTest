package com.example.aidlclienttest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aidlservertest.Bean.PersonBean;
import com.example.aidlservertest.Proxy.PersonManagerInterface;
import com.example.aidlservertest.Proxy.Stub;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button button,button2,button3;
    private TextView textView;

    private PersonManagerInterface personManagerInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prePareAidl();

        textView = findViewById(R.id.textview);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        //添加人员
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonBean personBean = new PersonBean();
                personBean.setName("张三");
                try {
                    personManagerInterface.addPerson(personBean);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                getPerson();
            }
        });
        //删除人员
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonBean personBean = new PersonBean();
                personBean.setName("张三");
                try {
                    personManagerInterface.deletePerson(personBean);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                getPerson();
            }
        });
        //获取人员
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPerson();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("--- onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("--- onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("--- onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("--- onStop");
    }

    private void getPerson() {
        try {
            List<PersonBean> list = personManagerInterface.getPerson();
            String str = "";
            for (PersonBean p:list){
                str += p.getName()+", ";
            }
            textView.setText(str);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法说明：通过绑定远程service，初始化远程iBinder实例
     * 日期：2020-09-09 17:14
     */
    private void prePareAidl() {
        Intent intent = new Intent();
        intent.setClassName("com.example.aidlservertest","com.example.aidlservertest.Service.PersonService");
        bindService(intent,MyServerConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 方法说明：创建ServiceConnection
     * 日期：2020-09-09 17:15
     */
    private ServiceConnection MyServerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            System.out.println("---客户端 建立服务链接; Thread: "+Thread.currentThread().getName());
            //初始化远程iBinder实例，将iBinder转成远程服务代理对象，也就是PersonManagerProxy类对象，并调用它的方法
            personManagerInterface = Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("---客户端 断开服务链接; Thread: "+Thread.currentThread().getName());
        }
    };

}
