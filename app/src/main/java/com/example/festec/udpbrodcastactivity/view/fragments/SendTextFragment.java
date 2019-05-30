package com.example.festec.udpbrodcastactivity.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.example.festec.udpbrodcastactivity.R;
import com.example.festec.udpbrodcastactivity.module.message.TextMessage;
import com.example.festec.udpbrodcastactivity.module.protocol.EmergencyProtocol;
import com.example.festec.udpbrodcastactivity.module.protocol.PackEmergencyProtocol;
import com.example.festec.udpbrodcastactivity.module.udp.UdpServer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTextFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SendTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendTextFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private EditText editText;
    private ToggleButton toggleButton;
    private UdpServer udpServer;
    private EmergencyProtocol<TextMessage> emergencyProtocol;

    // TODO: Rename and change types of parameters
    private String broadcastType;
    private String broadcastLevel;

    private OnTextFragmentInteractionListener mListener;


    public SendTextFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendTextFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendTextFragment newInstance(String param1, String param2) {
        SendTextFragment fragment = new SendTextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            broadcastType = getArguments().getString(ARG_PARAM1);
            broadcastLevel = getArguments().getString(ARG_PARAM2);
        }
        TextMessage textMessage = new TextMessage();
        PackEmergencyProtocol<TextMessage> packEmergencyProtocol = new PackEmergencyProtocol<>(getContext());
        emergencyProtocol = packEmergencyProtocol.packPackEmergencyProtocol();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_text, container, false);
        editText = view.findViewById(R.id.edit_send_text);
        toggleButton = view.findViewById(R.id.bt_send_text);
        udpServer = new UdpServer();
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    udpServer.start();
                } else {
                    udpServer.udpServerClose();
                }
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTextFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTextFragmentInteractionListener) {
            mListener = (OnTextFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTextFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTextFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTextFragmentInteraction(Uri uri);
    }
}
