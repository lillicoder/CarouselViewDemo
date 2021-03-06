/**
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

package com.lillicoder.demo.carouselview;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lillicoder.demo.carouselview.widget.CarouselView;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        CarouselView carousel = (CarouselView) findViewById(R.id.carousel);
        carousel.setAdapter(new CarouselView.Adapter<Pair<String, Integer>>(getCarouselItems()) {
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void onDestroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public Object onInstantiateItem(ViewGroup container, int position) {
                Pair<String, Integer> pair = getItem(position);

                final LayoutInflater inflater = LayoutInflater.from(container.getContext());
                TextView view = (TextView) inflater.inflate(R.layout.view_carousel_item, container, false);
                view.setBackgroundResource(pair.second);
                view.setText(pair.first);

                container.addView(view);

                return view;
            }
        });
    }

    /**
     * Gets a collection of {@link Pair} to back this demo's carousel.
     * @return Collection of data to back this demo's carousel.
     */
    private List<Pair<String, Integer>> getCarouselItems() {
        List<Pair<String, Integer>> items = new ArrayList<>();
        items.add(Pair.create("1", R.color.blue));
        items.add(Pair.create("2", R.color.red));
        items.add(Pair.create("3", R.color.purple));
        items.add(Pair.create("4", R.color.yellow));
        items.add(Pair.create("5", R.color.teal));

        return items;
    }

}
