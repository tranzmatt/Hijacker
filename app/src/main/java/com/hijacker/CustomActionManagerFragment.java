package com.hijacker;

/*
    Copyright (C) 2019  Christos Kyriakopoulos
    Copyright (C) 2022-2023  Christian <kimocoder> B.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.io.File;

import static com.hijacker.CustomAction.cmds;
import static com.hijacker.MainActivity.FRAGMENT_CUSTOM;
import static com.hijacker.MainActivity.actions_path;
import static com.hijacker.MainActivity.currentFragment;
import static com.hijacker.MainActivity.custom_action_adapter;
import static com.hijacker.MainActivity.mFragmentManager;

public class CustomActionManagerFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.custom_action_manager, container, false);

        ListView list = v.findViewById(R.id.list);
        list.setAdapter(custom_action_adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int index, long l){
                PopupMenu popup = new PopupMenu(getActivity(), view);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //add(groupId, itemId, order, title)
                popup.getMenu().add(0, 0, 0, getString(R.string.edit));
                popup.getMenu().add(0, 1, 1, getString(R.string.delete));

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(android.view.MenuItem item){
                        switch(item.getItemId()){
                            case 0:
                                //Open editor for this
                                CustomActionEditorFragment fragment = new CustomActionEditorFragment();
                                fragment.action = CustomAction.cmds.get(index);

                                FragmentTransaction ft = mFragmentManager.beginTransaction();
                                ft.replace(R.id.fragment1, fragment);
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                ft.addToBackStack(null);
                                ft.commitAllowingStateLoss();
                                break;
                            case 1:
                                //Delete action
                                CustomDialog dialog = new CustomDialog();
                                dialog.setTitle(getString(R.string.action_delete_title));
                                dialog.setMessage(getString(R.string.action_delete_message));
                                dialog.setPositiveButton(getString(R.string.delete), new Runnable(){
                                    @Override
                                    public void run(){
                                        new File(actions_path + "/" + cmds.get(index).getTitle() + ".action").delete();
                                        cmds.remove(index);
                                        custom_action_adapter.notifyDataSetChanged();
                                    }
                                });
                                dialog.setNegativeButton(getString(R.string.cancel), null);
                                dialog.show(getFragmentManager(), "CustomDialog for action delete");
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        FloatingActionButton fab = v.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Open editor for new
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.replace(R.id.fragment1, new CustomActionEditorFragment());
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            }
        });

        return v;
    }
    @Override
    public void onResume(){
        super.onResume();
        currentFragment = FRAGMENT_CUSTOM;
        ((MainActivity)getActivity()).refreshDrawer();
    }
}
