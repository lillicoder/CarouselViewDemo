/*
 * Copyright 2015 Scott Weeden-Moody
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lillicoder.demo.carouselview.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;
import com.lillicoder.demo.carouselview.R;
import junit.framework.Assert;

import java.util.List;

/**
 * View that displays a carousel of arbitrary views in a {@link ViewPager}.
 */
public class CarouselView extends LinearLayout {

    private ViewPager mViewPager;
    private ViewGroup mIndicatorContainer;

    private int mIndicatorDrawableResourceId;
    private int mIndicatorPadding;

    public CarouselView(Context context) {
        this(context, null);
    }

    public CarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CarouselView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle);
    }

    /**
     * Adds indicators to this carousels indicator container. All previously added
     * indicators will be removed.
     * @param amount Number of indicators to add.
     */
    private void addIndicators(int amount) {
        mIndicatorContainer.removeAllViews();

        for (int position = 0; position < amount; position++) {
            CheckedImageView indicator = new CheckedImageView(getContext());
            indicator.setImageResource(mIndicatorDrawableResourceId);
            indicator.setPadding(mIndicatorPadding, mIndicatorPadding, mIndicatorPadding, mIndicatorPadding);
            indicator.setChecked(position == 0); // Ensure first item is activated by default

            mIndicatorContainer.addView(indicator);
        }
    }

    /**
     * Inflates and initializes this view.
     * @param context {@link Context} for this view.
     * @param attrs {@link AttributeSet} for this view.
     * @param defStyle Optional default style for this view.
     */
    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_carousel, this);

        mViewPager = (ViewPager) findViewById(R.id.CarouselView_viewPager);
        mIndicatorContainer = (ViewGroup) findViewById(R.id.CarouselView_indicatorContainer);

        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CarouselView, defStyle, 0);
            Resources resources = getResources();

            mIndicatorDrawableResourceId =
                    attributes.getResourceId(R.styleable.CarouselView_indicator, R.drawable.carousel_indicator);
            mIndicatorPadding =
                    (int) attributes.getDimension(R.styleable.CarouselView_indicatorPadding,
                                                  resources.getDimension(R.dimen.indicator_padding));
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {}

            @Override
            public void onPageScrolled(int position, float floatOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                int childCount = mIndicatorContainer.getChildCount();
                for (int childPosition = 0; childPosition < childCount; childPosition++) {
                    Checkable child = (Checkable) mIndicatorContainer.getChildAt(childPosition);
                    if (child != null) {
                        child.setChecked(childPosition == position);
                    }
                }
            }
        });
    }

    /**
     * Sets the {@link Adapter} for this view.
     * @param adapter Adapter to set.
     */
    public void setAdapter(Adapter adapter) {
        addIndicators(adapter.getCount());
        mViewPager.setAdapter(adapter);
    }

    /**
     * <p>
     *     Adapter that backs a {@link CarouselView}.
     * </p>
     * <p>
     *     Since instantiating adapter items can return any kind of object,
     *     it is left to implementers to implement the following methods:
     *
     *     <ul>
     *         <li>{@link PagerAdapter#destroyItem(ViewGroup, int, Object)}</li>
     *         <li>{@link PagerAdapter#instantiateItem(ViewGroup, int)}</li>
     *         <li>{@link PagerAdapter#isViewFromObject(View, Object)}</li>
     *     </ul>
     * </p>
     */
    public abstract static class Adapter<T> extends PagerAdapter {

        private static final String PRECONDITION_NULL_OR_EMPTY_ITEMS =
            "Cannot instantiate a carousel adapter with a null or empty list of items.";

        private List<T> mItems;

        public Adapter(List<T> items) {
            Assert.assertTrue(PRECONDITION_NULL_OR_EMPTY_ITEMS, items != null && !items.isEmpty());
            mItems = items;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            onDestroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return onInstantiateItem(container, position);
        }

        /**
         * Gets the object for the adapter at the given position.
         * @param position Position of the object to get.
         * @return Object for this adapter at the given position.
         * @throws IndexOutOfBoundsException Thrown if the given position is out of the bounds of
         *         the list of items backing this adapter.
         */
        public T getItem(int position) {
            return mItems.get(position);
        }

        /**
         * Callback fired to remove a carousel page for the given position.
         * The adapter is responsible for removing the view from its container.
         * @param container The containing View from which the page will be removed.
         * @param position The page position to be removed.
         * @param object The same object that was returned by {@link #onInstantiateItem(ViewGroup, int)}.
         */
        public abstract void onDestroyItem(ViewGroup container, int position, Object object);

        /**
         * Callback fired to create the carousel page for the given position.
         * The adapter is responsible for adding the view to the container given here.
         * @param container The containing View in which the page will be shown.
         * @param position The page position to be instantiated.
         * @return Object representing the new page. This does not need to be a View,
         *         but can be some other container of the page.
         */
        public abstract Object onInstantiateItem(ViewGroup container, int position);

    }

}
