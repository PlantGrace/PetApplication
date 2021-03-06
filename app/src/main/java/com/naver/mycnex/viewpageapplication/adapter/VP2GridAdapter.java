package com.naver.mycnex.viewpageapplication.adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.naver.mycnex.viewpageapplication.R;
import com.naver.mycnex.viewpageapplication.custom.SquareImageView;
import com.naver.mycnex.viewpageapplication.data.Bookmark;
import com.naver.mycnex.viewpageapplication.data.ImageFile;
import com.naver.mycnex.viewpageapplication.data.Store;
import com.naver.mycnex.viewpageapplication.glide.GlideApp;
import com.naver.mycnex.viewpageapplication.global.Global;
import com.naver.mycnex.viewpageapplication.gps.GpsInfo;
import com.naver.mycnex.viewpageapplication.login.LoginService;
import com.naver.mycnex.viewpageapplication.retrofit.RetrofitService;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Data
@ToString
public class VP2GridAdapter extends BaseAdapter{

    ArrayList<Store> stores;
    ArrayList<ImageFile> images;
    ArrayList<Bookmark> bookmarks;
    HashMap<Long,Bookmark> hBookMarks;
    Integer[] reviews;
    boolean turnOn = true;

    public VP2GridAdapter(ArrayList<Store> stores, ArrayList<ImageFile> images, Integer[] reviews, ArrayList<Bookmark> bookmarks) {
        this.stores = stores;
        this.images = images;
        this.reviews = reviews;
        if (bookmarks != null) {
            this.bookmarks = bookmarks;
            hBookMarks = new HashMap<>();
            for (int i = 0 ; i < bookmarks.size() ; i++) { // 생성할 때 HashMap을 생성해서 key 값에 데이터를 넣어놓기
                hBookMarks.put(bookmarks.get(i).getStore_id(),bookmarks.get(i)); // value 값은 상관없고 key에 store_id를 넣어놓는게 중요. -> null 인지만 검사하기 때문.
            }
        } else {
            this.bookmarks = null;
        }
    }

    @Override
    public int getCount() {
        return stores.size();
    }
    @Override
    public Object getItem(int position) {
        return stores.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        Holder holder = new Holder();

        if( convertView == null ){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vp2_grid, parent, false);
            holder.itemImg = convertView.findViewById(R.id.itemImg);
            holder.btnBookmark = convertView.findViewById(R.id.btnBookmark);
            holder.textName = convertView.findViewById(R.id.textName);
            holder.textPlace = convertView.findViewById(R.id.textPlace);
            holder.textDistance = convertView.findViewById(R.id.TextDistance);
            holder.textPoint = convertView.findViewById(R.id.TextPoint);
            holder.view_vp2_txt = convertView.findViewById(R.id.view_vp2_txt);
            holder.review_count_txt = convertView.findViewById(R.id.review_count_txt);

            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        // 그리드뷰 아이템 세팅
        final Store store = (Store) getItem(position);
        // Context
        Context context = parent.getContext();

        /** setText **/

        //장소이름
        holder.textName.setText(store.getName());

        //현재위치로부터의 거리
        holder.textDistance.setText(getDistance(context,position) + "m"); // TODO : ( 구현? 삭제? )

        //리뷰 수
        holder.review_count_txt.setText(reviews[position].toString());

        //카테고리 ( 장소구분 )
        // DB 필드값 : Global 클래스의 CATEGORY_GENERAL_ARR 배열에서 인덱스 값으로 사용할 수 있도록 설계
        if( store.getCategory() >= Global.CATEGORY_DIVISION_NUM) {
            holder.textPlace.setText(Global.CATEGORY_SPECIAL_STR_ARR[ store.getCategory()-Global.CATEGORY_SPECIAL_CAFE ]);
        } else {
            holder.textPlace.setText(Global.CATEGORY_GENERAL_STR_ARR[ store.getCategory() ]);
        }
        // TODO :
        // 평점 계산 로직)
        // 리뷰가 작성될 때마다
        // 리뷰카운트 & 점수(합계) 를 저장해서
        // 불러올 때 리뷰점수 / 리뷰카운트 로 점수를 계산
         /*DB 로부터 데이터를 받아올 때에
         해당 Store 에 달린 리뷰의 점수를 모두 더하여
         리뷰의 갯수만큼 나눈 다음에
         - 객체 배열에 넣어서 사용*/

        double result = ((double)store.getScore_sum())/((double)store.getScore_count());
        double getPrimeNum = Math.ceil(result*10d) / 10d;

        if (Double.isNaN(getPrimeNum)) {
            holder.textPoint.setText("0.0");
        } else {
            holder.textPoint.setText(String.valueOf(getPrimeNum));
        }

        holder.view_vp2_txt.setText(store.getHit().toString());

        /** setIMG **/

        // 그리드 이미지
        GlideApp.with(context)
                .load(Global.BASE_IMAGE_URL+images.get(position).getSavedName())
                .centerCrop()
                .into( holder.itemImg );

        // 북마크 여부 표시 ( TODO : 북마크 여부에 따라 다른 이미지 적용 )
        if (bookmarks == null) {
            GlideApp.with(context)
                    .load( R.drawable.star_off )
                    .fitCenter()
                    .into( holder.btnBookmark );
        } else {
            if (hBookMarks.get(store.getId()) == null) {
                GlideApp.with(context)
                        .load( R.drawable.star_off )
                        .fitCenter()
                        .into( holder.btnBookmark );
            } else {
                GlideApp.with(context)
                        .load( R.drawable.star_on )
                        .fitCenter()
                        .into( holder.btnBookmark );
            }
        }

        /** OnClick **/
        final Holder onClickHolder = holder;
        holder.btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : 북마크에 저장, 북마크 여부에 따른  이미지 교체
                LoginService loginService = LoginService.getInstance();
                if (loginService.getLoginMember() != null) {
                    if (turnOn) {
                        Call<Void> AddBookMark = RetrofitService.getInstance().getRetrofitRequest().AddBookMark(store.getId(),loginService.getLoginMember().getId());
                        AddBookMark.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {

                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });

                        GlideApp.with(parent.getContext())
                                .load(R.drawable.star_on)
                                .fitCenter()
                                .into(onClickHolder.btnBookmark);
                        turnOn = false;
                    } else {
                        Call<Void> DeleteBookMark = RetrofitService.getInstance().getRetrofitRequest().DeleteBookMark(store.getId(),loginService.getLoginMember().getId());
                        DeleteBookMark.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {

                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                            }
                        });

                        GlideApp.with(parent.getContext())
                                .load(R.drawable.star_off)
                                .fitCenter()
                                .into(onClickHolder.btnBookmark);
                        turnOn = true;
                    }
                } else {
                    Toast.makeText(parent.getContext(), "로그인 후 사용하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    public int getDistance(Context context, int position) {

        LatLng latLng = getCurrentLocationByGPS(context);

        Location location1 = new Location("location1");
        location1.setLatitude(latLng.latitude);
        location1.setLongitude(latLng.longitude);

        Location location2 = new Location("location2");
        location2.setLatitude(stores.get(position).getLatitude());
        location2.setLongitude(stores.get(position).getLongitude());

        int distance = (int)location1.distanceTo(location2);

        return distance;
    }

    public LatLng getCurrentLocationByGPS(Context context) {
        GpsInfo gps = new GpsInfo(context);

        double lat = 0;
        double lng = 0;

        if (gps.isGetLocation()) {

            lat = gps.getLatitude();
            lng = gps.getLongitude();

        }

        LatLng currentLocation = new LatLng(lat, lng);
        return currentLocation;
    }

    public class Holder {
        SquareImageView itemImg;
        ImageView btnBookmark;
        TextView textName;
        TextView textDistance;
        TextView textPlace;
        TextView textPoint;
        TextView view_vp2_txt;
        TextView review_count_txt;
    }
}
