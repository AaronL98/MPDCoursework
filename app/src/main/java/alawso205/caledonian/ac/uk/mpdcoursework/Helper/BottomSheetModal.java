package alawso205.caledonian.ac.uk.mpdcoursework.Helper;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import alawso205.caledonian.ac.uk.mpdcoursework.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Aaron Lawson (S1624910)
 */
public class BottomSheetModal extends BottomSheetDialogFragment {

    private BottomSheetListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        LinearLayout btnCurrentIncidents = v.findViewById(R.id.btnCurrentIncidents);
        LinearLayout btnRoadworks = v.findViewById(R.id.btnRoadworks);
        LinearLayout btnPlannedRoadworks = v.findViewById(R.id.btnPlannedRoadworks);

        btnCurrentIncidents.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listener.onClicked(RSSItemType.CURRENT_INCIDENTS);
                dismiss();
            }
        });
        btnRoadworks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listener.onClicked(RSSItemType.ROADWORKS);
                dismiss();
            }
        });
        btnPlannedRoadworks .setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listener.onClicked(RSSItemType.PLANNED_ROADWORKS);
                dismiss();
            }
        });

         return v;
    }

    public interface BottomSheetListener{
        void onClicked(RSSItemType rssItemType);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (BottomSheetListener) context;
    }
}
