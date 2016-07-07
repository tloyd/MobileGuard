package cc.springwind.mobileguard.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cc.springwind.mobileguard.R;
import cc.springwind.mobileguard.base.BaseActivity;
import cc.springwind.mobileguard.db.dao.CommonNumberDao;

/**
 * Created by HeFan on 2016/7/5.
 */
public class CommonNumberActivity extends BaseActivity {
    @InjectView(R.id.elv_common_number)
    ExpandableListView elvCommonNumber;
    private List<CommonNumberDao.Group> groupList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
        CommonNumberDao dao = new CommonNumberDao();
        groupList = dao.getGroup();

        IAdapter adapter = new IAdapter();
        elvCommonNumber.setAdapter(adapter);

    }

    class IAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return groupList.get(groupPosition).childList.size();
        }

        @Override
        public CommonNumberDao.Group getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public CommonNumberDao.Child getChild(int groupPosition, int childPosition) {
            return groupList.get(groupPosition).childList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("			"+getGroup(groupPosition).name);
            textView.setTextColor(Color.RED);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_number = (TextView) view.findViewById(R.id.tv_number);

            tv_name.setText(getChild(groupPosition, childPosition).name);
            tv_number.setText(getChild(groupPosition, childPosition).number);

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
