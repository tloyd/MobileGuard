package cc.springwind.mobileguard.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;

/**
 * Created by HeFan on 2016/6/30.
 */
public class ContactsListActivity extends BaseActivity {
    @InjectView(R.id.lv_contact)
    ListView lvContact;
    private ContactsListAdapter mAdapter;
    private HashMap<String, String> hashMap;
    private ArrayList<HashMap<String, String>> contactList = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mAdapter=new ContactsListAdapter();
                    lvContact.setAdapter(mAdapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        ButterKnife.inject(this);

        initUI();
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                Cursor cursor = getContentResolver().query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"}, null, null, null);
                contactList.clear();
                while (cursor.moveToNext()) {
                    String contact_id = cursor.getString(0);
                    Cursor dataCursor = getContentResolver().query(Uri.parse("content://com.android.contacts/data"), new
                            String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{contact_id}, null);
                    hashMap = new HashMap<String, String>();
                    while (dataCursor.moveToNext()) {
                        String data1 = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);
                        if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                            //数据非空判断
                            if (!TextUtils.isEmpty(data1)) {
                                hashMap.put("phone", data1);
                            }
                        } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                            if (!TextUtils.isEmpty(data1)) {
                                hashMap.put("name", data1);
                            }
                        }
                    }
                    dataCursor.close();
                    contactList.add(hashMap);
                }
                cursor.close();
                mHandler.sendEmptyMessage(1);
            }
        }.start();
    }

    private void initUI() {
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter!=null){
                    HashMap<String, String> item = mAdapter.getItem(position);
                    String phone = item.get("phone");
                    Intent intent=new Intent();
                    intent.putExtra("phone",phone);
                    setResult(0,intent);
                    finish();
                }
            }
        });
    }

    class ContactsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView=View.inflate(ContactsListActivity.this,R.layout.item_contact_listview,null);
            }
            TextView tv_name= (TextView) convertView.findViewById(R.id.tv_name);
            TextView tv_phone= (TextView) convertView.findViewById(R.id.tv_phone);
            tv_name.setText(getItem(position).get("name"));
            tv_phone.setText(getItem(position).get("phone"));
            return convertView;
        }
    }
}
