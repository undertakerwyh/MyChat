package com.wyh.mychat.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 导航leadActivity的viewPager
 */
public class ViewPagerAdapter extends PagerAdapter{

    private Context context;
    /**保存viewpager的view视图*/
    private List<View>viewList = new ArrayList<View>();
    /**保存viewpager的文字*/
    private List<String>tabtitleList = new ArrayList<>();

    public ViewPagerAdapter(Context context){
        this.context = context;
    }
    /**添加view视图*/
    public void addToViewAdapter(View view){
        viewList.add(view);
    }
    /**添加viewpager的文字*/
    public void addToTextAdapter(String string){
        tabtitleList.add(string);
    }
    /**获取Viewpager的view的集合*/
    public List<View>getViewList(){
        return viewList;
    }
    /**viewpager 的方法*/
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = viewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = viewList.get(position);
        container.removeView(view);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitleList.get(position);
    }

    @Override
    public int getCount() {

        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
