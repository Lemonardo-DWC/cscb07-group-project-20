package com.example.smartair;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class ParentHomeFragment
        extends Fragment
        implements ParentHomeChildItemAdapter.OnDetailButtonClick {

    // buttons
    private MaterialButton menuButton;
    private MaterialButtonToggleGroup trendRangeToggleGroup;

    // recycler views
    private RecyclerView childRecycler;

    // child item list
    private List<ChildItem> childItemList;
    private ParentHomeChildItemAdapter parentHomeChildItemAdapter;

    // helper class objects
    private ParentHomeViewModel phvm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_home, container, false);

        phvm = new ViewModelProvider(requireActivity()).get(ParentHomeViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // button initialization
        menuButton = view.findViewById(R.id.menu_button);
        trendRangeToggleGroup = view.findViewById(R.id.trendRangeToggleGroup);

        // button behaviour
        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), menuButton);
            popupMenu.getMenuInflater().inflate(R.menu.parent_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if(itemId == R.id.manageChildren) {
                    ((MainActivity) requireActivity()).loadFragment(new ManageChildFragment());
                    return true;
                } else if (itemId == R.id.shareReport) {
                    Toast.makeText(
                            requireContext(),
                            "share report",
                            Toast.LENGTH_SHORT
                    ).show();
                    return true;
                } else if (itemId == R.id.viewInventory) {
                    ((MainActivity) requireActivity()).loadFragment(new ParentInventoryFragment());
                    return true;
                } else if (itemId == R.id.useChildProfile) {
                    Toast.makeText(
                            requireContext(),
                            "use child profile",
                            Toast.LENGTH_SHORT
                    ).show();
                    return true;
                } else if (itemId == R.id.shareWithProvider) {
                    ((MainActivity) requireActivity()).loadFragment(new ShareWithProviderFragment());
                    return true;
                } else if (itemId == R.id.logout) {
                    phvm.logout();
                    ((MainActivity) requireActivity()).loadFragment(new LoginFragment());
                }

                return false;

            });

            popupMenu.show();

        });

        trendRangeToggleGroup.addOnButtonCheckedListener(
                new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup materialButtonToggleGroup,
                                        int checkedId, boolean isChecked) {
                if(isChecked){
                    if (checkedId == R.id.buttonSevenDay) {
                        Toast.makeText(
                                requireContext(),
                                "Selected 7 day trend",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else if (checkedId == R.id.buttonThirtyDay) {
                        Toast.makeText(
                                requireContext(),
                                "selected 30 day trend",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            }
        });

        // child status recycler setup
        childRecycler = view.findViewById(R.id.childRecycler);

        childItemList = new ArrayList<>();
        parentHomeChildItemAdapter = new ParentHomeChildItemAdapter(childItemList, this, requireContext());
        childRecycler.setAdapter(parentHomeChildItemAdapter);
        childRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        phvm.trackChildListRef();

        phvm.childItemListData.observe(getViewLifecycleOwner(), newChildItemList -> {
            childItemList.clear();
            childItemList.addAll(newChildItemList);
            parentHomeChildItemAdapter.notifyDataSetChanged();
        });

        /// back button handling ///
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {

            /// home screen is navigation root after logging in, thus back button should send user
            /// out of app
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };
        requireActivity()
                .getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onClick(ChildItem childItem) {
        phvm.selectItem(childItem);

        ((MainActivity) requireActivity()).loadFragment(new ChildDetailsFragment());
    }
}