package cc.springwind.mobileguard.db.entity;

import android.graphics.drawable.Drawable;

/**
 * Created by HeFan on 2016/7/5.
 */
public class ProcessInfoBean {
    public String name;//应用名称
    public Drawable icon;//应用图标
    public long memSize;//应用已使用的内存数
    public boolean isCheck;//是否被选中
    public boolean isSystem;//是否为系统应用
    public String packageName;//如果进程没有名称,则将其所在应用的包名最为名称
}
