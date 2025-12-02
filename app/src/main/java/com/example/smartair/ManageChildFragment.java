package com.example.smartair;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ManageChildFragment extends Fragment {

    private ParentHomeViewModel phvm;

    private List<ChildItem> childItemList;
    private RecyclerView childListRecycler;
    private ChildItemListAdapter childItemListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_child, container, false);

        phvm = new ViewModelProvider(requireActivity()).get(ParentHomeViewModel.class);

        Button buttonAddChild = view.findViewById(R.id.exampleButton);

        buttonAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).loadFragment(new AddChildFragment());
            }
        });

        Button backButton = view.findViewById(R.id.manage_children_back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // child status recycler setup
        childListRecycler = view.findViewById(R.id.childListRecycler);

        childItemList = new ArrayList<>();
        childItemListAdapter = new ChildItemListAdapter(childItemList);
        childListRecycler.setAdapter(childItemListAdapter);
        childListRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        phvm.childItemListData.observe(getViewLifecycleOwner(), newChildItemList -> {
            childItemList.clear();
            childItemList.addAll(newChildItemList);
            childItemListAdapter.notifyDataSetChanged();
        });

    }
}