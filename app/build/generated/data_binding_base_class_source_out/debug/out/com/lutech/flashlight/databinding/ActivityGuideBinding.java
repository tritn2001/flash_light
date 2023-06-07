// Generated by view binder compiler. Do not edit!
package com.lutech.flashlight.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.lutech.flashlight.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityGuideBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageView ivBack;

  @NonNull
  public final RelativeLayout rlHead;

  @NonNull
  public final RecyclerView rvGuide;

  private ActivityGuideBinding(@NonNull ConstraintLayout rootView, @NonNull ImageView ivBack,
      @NonNull RelativeLayout rlHead, @NonNull RecyclerView rvGuide) {
    this.rootView = rootView;
    this.ivBack = ivBack;
    this.rlHead = rlHead;
    this.rvGuide = rvGuide;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityGuideBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityGuideBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_guide, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityGuideBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ivBack;
      ImageView ivBack = ViewBindings.findChildViewById(rootView, id);
      if (ivBack == null) {
        break missingId;
      }

      id = R.id.rlHead;
      RelativeLayout rlHead = ViewBindings.findChildViewById(rootView, id);
      if (rlHead == null) {
        break missingId;
      }

      id = R.id.rvGuide;
      RecyclerView rvGuide = ViewBindings.findChildViewById(rootView, id);
      if (rvGuide == null) {
        break missingId;
      }

      return new ActivityGuideBinding((ConstraintLayout) rootView, ivBack, rlHead, rvGuide);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}