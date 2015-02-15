package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private ListView item_list;
    //private ArrayList<String> data = new ArrayList<>();
    //private ArrayAdapter<String> adapter;

    // ListView使用的自定Adapter物件
    private ItemAdapter itemAdapter;
    // 儲存所有記事本的List物件
    private List<Item> items;

    // 選單項目物件
    private MenuItem add_item, search_item, revert_item, share_item, delete_item;

    // 已選擇項目數量
    private int selectedCount = 0;

    private ItemDAO itemDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processViews();
        processControllers();

        // 设定list列表
//        data.add("關於Android Tutorial的事情");
//        data.add("一隻非常可愛的小狗狗!");
//        data.add("一首非常好聽的音樂！");
//
//        int layoutId = android.R.layout.simple_list_item_1;
//        adapter = new ArrayAdapter<String>(this, layoutId, data);
//
//        item_list.setAdapter(adapter);

        // 加入範例資料
//        items = new ArrayList<Item>();
//
//        items.add(new Item(1, new Date().getTime(), Colors.RED, "關於Android Tutorial的事情.", "Hello content", "", 0, 0, 0));
//        items.add(new Item(2, new Date().getTime(), Colors.BLUE, "一隻非常可愛的小狗狗!", "她的名字叫「大熱狗」，又叫\n作「奶嘴」，是一隻非常可愛\n的小狗。", "", 0, 0, 0));
//        items.add(new Item(3, new Date().getTime(), Colors.GREEN, "一首非常好聽的音樂！", "Hello content", "", 0, 0, 0));

        // 建立数据库
        itemDAO = new ItemDAO(getApplicationContext());
        if(itemDAO.getCount() == 0) {
            itemDAO.sample();
        }
        items = itemDAO.getAll();

        // 建立自定Adapter物件
        itemAdapter = new ItemAdapter(this, R.layout.single_item, items);
        item_list.setAdapter(itemAdapter);
    }

    private void processViews() {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // 列表控件 显示项目
        item_list = (ListView)findViewById(R.id.item_list);
    }

    private void processControllers() {

        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Item item = itemAdapter.getItem(position);

                if(selectedCount > 0) {
                    processMenu(item);
                    itemAdapter.set(position, item);
                }
                else {
                    // 使用Action名稱建立啟動另一個Activity元件需要的Intent物件
                    Intent intent = new Intent(".EDIT_ITEM");

                    // 設定記事編號與標題
                    intent.putExtra("position", position);
                    intent.putExtra(".Item", item);

                    // 呼叫「startActivityForResult」，第二個參數「1」表示執行修改
                    startActivityForResult(intent, 1);
                }
            }
        };
        item_list.setOnItemClickListener(itemListener);

        AdapterView.OnItemLongClickListener itemLongListener = new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Item item = itemAdapter.getItem(position);
                processMenu(item);
                itemAdapter.set(position, item);
                return true;
            }
        };
        item_list.setOnItemLongClickListener(itemLongListener);

    }

    private void processMenu(Item item) {
        // 如果需要設定記事項目
        if (item != null) {
            // 設定已勾選的狀態
            item.setSelected(!item.isSelected());

            // 計算已勾選數量
            if (item.isSelected()) {
                selectedCount++;
            }
            else {
                selectedCount--;
            }
        }

        // 根據選擇的狀況，設定是否顯示選單項目
        add_item.setVisible(selectedCount == 0);
        search_item.setVisible(selectedCount == 0);
        revert_item.setVisible(selectedCount > 0);
        share_item.setVisible(selectedCount > 0);
        delete_item.setVisible(selectedCount > 0);
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    // 重载函数 activity返回事件
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            // 讀取記事物件
            Item item = (Item) data.getExtras().getSerializable(".Item");

            // 新增记事
            if (requestCode == 0) {
//                item.setId(items.size() + 1);
//                item.setDatetime(new Date().getTime());
                item = itemDAO.insert(item);

                items.add(item);

                // 通知資料已經改變，ListView元件才會重新顯示
                itemAdapter.notifyDataSetChanged();
            }
            // 修改记事
            else if (requestCode == 1) {
                // 讀取記事編號

                int position = data.getIntExtra("position", -1);

                if (position != -1) {
                    itemDAO.update(item);
                    // 設定標題項目
                    items.set(position, item);
                    // 通知資料已經改變，ListView元件才會重新顯示
                    itemAdapter.notifyDataSetChanged();
                }

            }
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
//                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                startActivity(new Intent(this, WebActivity.class));
                break;
            case 4:
//                mTitle = getString(R.string.title_section3);
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_menu, menu);
            restoreActionBar();

            // 取得選單項目物件
            add_item = menu.findItem(R.id.add_item);
            search_item = menu.findItem(R.id.search_item);
            revert_item = menu.findItem(R.id.revert_item);
            share_item = menu.findItem(R.id.share_item);
            delete_item = menu.findItem(R.id.delete_item);

            // 設定選單項目
            processMenu(null);

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickMenuItem(MenuItem item) {
        int item_id = item.getItemId();
        switch(item_id) {
            case R.id.search_item:
                // TODO: Add search function
                break;
            case R.id.add_item:
                Intent intent = new Intent(".ADD_ITEM");
                // 呼叫「startActivityForResult」，，第二個參數「0」表示執行新增
                startActivityForResult(intent, 0);
                break;
            case R.id.revert_item:
                for (int i = 0; i < itemAdapter.getCount(); i++) {
                    Item ri = itemAdapter.getItem(i);

                    if (ri.isSelected()) {
                        ri.setSelected(false);
                        itemAdapter.set(i, ri);
                    }
                }
                selectedCount = 0;
                processMenu(null);
                break;
            case R.id.delete_item:
                // 沒有選擇
                if (selectedCount == 0) {
                    break;
                }

                // 建立與顯示詢問是否刪除的對話框
                AlertDialog.Builder d = new AlertDialog.Builder(this);
                String message = getString(R.string.delete_item);
                d.setTitle(R.string.delete)
                        .setMessage(String.format(message, selectedCount));
                d.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 刪除所有已勾選的項目
                                int index = itemAdapter.getCount() - 1;

                                while (index > -1) {
                                    Item item = itemAdapter.get(index);

                                    if (item.isSelected()) {
                                        itemAdapter.remove(item);
                                        // 从数据库中删除
                                        itemDAO.delete(item.getId());
                                    }

                                    index--;
                                }
                                // 通知資料改變
                                itemAdapter.notifyDataSetChanged();

                                // 回复原有菜单
                                selectedCount = 0;
                                processMenu(null);
                            }
                        });
                d.setNegativeButton(android.R.string.no, null);
                d.show();
                break;
            case R.id.googleplus_item:
                break;
            case R.id.facebook_item:
                break;
            case R.id.about_item:
                intent = new Intent(this, AboutActivity.class);
                // 呼叫「startActivity」，參數為一個建立好的Intent物件
                // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
                startActivity(intent);
                break;
        }
    }

    public void clickPreferences(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
