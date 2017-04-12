package com.aikaifa.checkall.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.aikaifa.checkall.R;
import com.aikaifa.checkall.adapter.ListAdapter;
import com.aikaifa.checkall.bean.Book;
import com.aikaifa.checkall.bean.SelectEvent;
import com.aikaifa.checkall.widget.OnStartDragListener;
import com.aikaifa.checkall.widget.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
/**
 * 微信公众号 aikaifa 欢迎关注
 */
public class MainActivity extends Activity  implements OnStartDragListener {
    private RecyclerView recyclerView;
    private CheckBox checkbox;
    private TextView selected;
    private ListAdapter adapter;
    private EventBus event;
    private boolean isChange = false;
    private ArrayList<Book> list = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        event = EventBus.getDefault();
        event.register(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        selected = (TextView) findViewById(R.id.selected);
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            Book model = new Book();
            model.setId(i);
            model.setName("商品" + i);
            model.setDesc("描述" + i);
            list.add(model);
        }
        adapter = new ListAdapter(list,this, event);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
                    int count = 0;
                    if (isChecked) {
                        isChange = false;
                    }
                    for (int i = 0, p = list.size(); i < p; i++) {
                        if (isChecked) {
                            map.put(i, true);
                            count++;
                        } else {
                            if (!isChange) {
                                map.put(i, false);
                                count = 0;
                            } else {
                                map = adapter.getMap();
                                count = map.size();
                            }
                        }
                    }
                    selected.setText("已选" + count + "项");
                    adapter.setMap(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        adapter.setOnItemClickListener(new ListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int positon) {
                Log.e("onItemClick", "" + positon);
            }

            @Override
            public void onItemLongClick(final RecyclerView.ViewHolder holder, final int positon) {
                Log.e("onItemLongClick", "" + positon);
            }
        });
    }

    public void onEventMainThread(SelectEvent event) {
        int size = event.getSize();
        if (size < list.size()) {
            isChange = true;
            checkbox.setChecked(false);
        } else {
            checkbox.setChecked(true);
            isChange = false;
        }
        selected.setText("已选" + size + "项");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        event.unregister(this);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
