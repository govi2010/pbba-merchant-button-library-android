package com.zapp.library.merchant.ui.view;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zapp.library.merchant.R;
import com.zapp.library.merchant.network.AvailableBankAppsLoader;
import com.zapp.library.merchant.network.response.PBBABankLogoResponse;
import com.zapp.library.merchant.util.FilterAvailableBankAppsBySmallLogo;
import com.zapp.library.merchant.util.PBBAAppUtils;
import com.zapp.library.merchant.util.PBBACustomButtonType;

/**
 * The Custom Pay by Bank app button.
 *
 * <p>
 * To display it in a layout, insert the following snippet: <br>
 * {@code
 * <com.zapp.library.merchant.ui.view.PBBACustomButton
 * android:id="@+id/pbbaCustomButton"
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content" />
 * <p>
 * To set layout type should you use {@link #setLayoutType(PBBACustomButtonType)}
 * <p>
 * }
 */
public class PBBACustomButton extends RelativeLayout implements LoaderManager.LoaderCallbacks<PBBABankLogoResponse>, View.OnClickListener {
    private static final String TAG = PBBACustomButton.class.getSimpleName();
    private static final int AVAILABLE_BANK_APPS_LOADER_ID = 3;
    private LinearLayout availableBankApssLayout;
    private LinearLayout moreAboutLayout;
    private LoaderManager loaderManager;
    private RadioButton radioButton;
    private int minWidth;
    private OnClickedView onClickedView;
    private CfiLogosCustomView cfiCustomView;
    private int columnWidth;

    /**
     * Creates a new Custom Pay by Bank app button
     *
     * @param context The view context.
     */
    public PBBACustomButton(Context context) {
        this(context, null);
    }

    /**
     * Creates a new Custom Pay by Bank app button
     *
     * @param context The view context.
     * @param attrs   The styled attributes.
     */
    public PBBACustomButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates a new Custom Pay by Bank app button
     *
     * @param context      The view context.
     * @param attrs        The styled attributes.
     * @param defStyleAttr The style
     */
    public PBBACustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflateView();
    }

    private void inflateView() {
        minWidth = getContext().getResources().getDimensionPixelOffset(R.dimen.pbba_custom_min_width);
        columnWidth = getContext().getResources().getDimensionPixelSize(R.dimen.default_column_width);
        final View view = inflate(getContext(), R.layout.pbba_custom_button_pay_by_bank_app, this);
        final TextView pbbaCustomButtonMoreAboutLink = (TextView) view.findViewById(R.id.pbba_custom_button_more_about_link);
        radioButton = (RadioButton) view.findViewById(R.id.radioButton);
        availableBankApssLayout = (LinearLayout) view.findViewById(R.id.pbba_popup_available_banking_app);
        moreAboutLayout = (LinearLayout) view.findViewById(R.id.pbba_button_more_about_container);
        cfiCustomView = (CfiLogosCustomView) view.findViewById(R.id.cfiCustomView);

        view.setOnClickListener(this);
        moreAboutLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PBBAAppUtils.showPBBAPopupAbout(((FragmentActivity) getContext()));
            }
        });

        pbbaCustomButtonMoreAboutLink.setPaintFlags(pbbaCustomButtonMoreAboutLink.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        setLayoutType(PBBACustomButtonType.NONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        if (widthSize < minWidth) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(minWidth, widthMode), heightMeasureSpec);
            calculateNumbersOfCfiLogosPerLine(minWidth);
        } else {
            if (getLayoutParams().width == LinearLayout.LayoutParams.WRAP_CONTENT) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(minWidth, widthMode), heightMeasureSpec);
                calculateNumbersOfCfiLogosPerLine(minWidth);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                calculateNumbersOfCfiLogosPerLine(widthSize);
            }
        }
    }

    private void calculateNumbersOfCfiLogosPerLine(int minWidth) {
        if (radioButton.getMeasuredWidth() > 0) {
           final int imageCountPerLine = Math.max(1, (minWidth - radioButton.getMeasuredWidth()) / columnWidth);
           cfiCustomView.setNumberOfCfiLogosPerLine(imageCountPerLine);
        }
    }

    /**
     * Set layout type see {@link PBBACustomButtonType}
     *
     * @param type
     */
    public void setLayoutType(@NonNull final PBBACustomButtonType type) {
        if (type == PBBACustomButtonType.NONE) {
            availableBankApssLayout.setVisibility(GONE);
            moreAboutLayout.setVisibility(GONE);
        }

        if (type == PBBACustomButtonType.MOREABOUT) {
            availableBankApssLayout.setVisibility(GONE);
            moreAboutLayout.setVisibility(VISIBLE);
        }

        if (type == PBBACustomButtonType.BANKLOGOS) {
            startLoaderManager();
            moreAboutLayout.setVisibility(GONE);
        }

        if (type == PBBACustomButtonType.MOREABOUT_AND_BANKLOGOS) {
            startLoaderManager();
            moreAboutLayout.setVisibility(VISIBLE);
        }
    }

    private void startLoaderManager() {
        if (loaderManager != null) {
            loaderManager.restartLoader(AVAILABLE_BANK_APPS_LOADER_ID, null, this);
        }
    }

    @Override
    public void onClick(View v) {
        notifyClickEvent();
    }

    public void setLoaderManager(@NonNull final LoaderManager loaderManager) {
        this.loaderManager = loaderManager;
    }

    @Override
    public Loader<PBBABankLogoResponse> onCreateLoader(final int id, final Bundle args) {
        return new AvailableBankAppsLoader(getContext(), new FilterAvailableBankAppsBySmallLogo());
    }

    @Override
    public void onLoadFinished(final Loader<PBBABankLogoResponse> loader, final PBBABankLogoResponse data) {
        Log.v(TAG, "onLoadFinished");
        updatePbBaCustomButtonUI(data);
    }

    @Override
    public void onLoaderReset(final Loader<PBBABankLogoResponse> loader) {
        Log.e(TAG, String.format("onLoaderReset (%s): loader: %s", this, loader));
    }

    private void updatePbBaCustomButtonUI(@NonNull final PBBABankLogoResponse pbbaBankLogoResponse) {
        if (pbbaBankLogoResponse.getAvailableBankAppsResponseList() != null && pbbaBankLogoResponse.getAvailableBankAppsResponseList().size() > 0) {
            cfiCustomView.setListOfAvailableBankAppsResponse(pbbaBankLogoResponse.getAvailableBankAppsResponseList());
            availableBankApssLayout.setVisibility(View.VISIBLE);
        } else {
            availableBankApssLayout.setVisibility(View.GONE);
        }
    }

    private void notifyClickEvent() {
        radioButton.setChecked(true);
        onClickedView.onClickedView();
    }

    public void setOnClickedView(OnClickedView onClickedView) {
        this.onClickedView = onClickedView;
    }

    public interface OnClickedView {
        void onClickedView();
    }
}
