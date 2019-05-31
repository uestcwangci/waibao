package com.example.festec.udpbrodcastactivity.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.festec.udpbrodcastactivity.R;
import com.example.festec.udpbrodcastactivity.module.GlobalValues;
import com.example.festec.udpbrodcastactivity.module.message.BaseMessage;
import com.example.festec.udpbrodcastactivity.module.protocol.EmergencyProtocol;
import com.example.festec.udpbrodcastactivity.module.protocol.PackEmergencyProtocol;
import com.example.festec.udpbrodcastactivity.module.udp.UdpServer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnBaseFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SendBaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendBaseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String TAG = "waibao";



    private static UdpServer udpServer;

    private EmergencyProtocol<BaseMessage> emergencyProtocol;
    private BaseMessage baseMessage;

    // TODO: Rename and change types of parameters
    private String broadcastType;
    private String broadcastLevel;
    private String dataType;

    private OnBaseFragmentInteractionListener mListener;


    public SendBaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param param3 Parameter 3.
     * @return A new instance of fragment SendBaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendBaseFragment newInstance(String param1, String param2, String param3) {
        SendBaseFragment fragment = new SendBaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            broadcastType = getArguments().getString(ARG_PARAM1);
            broadcastLevel = getArguments().getString(ARG_PARAM2);
            dataType = getArguments().getString(ARG_PARAM3);
        }
        baseMessage = new BaseMessage(broadcastType, broadcastLevel, broadcastType);
        udpServer = new UdpServer(GlobalValues.portList);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = null;
        switch (dataType) {
            case "文本":
                view = inflater.inflate(R.layout.fragment_send_text, container, false);
                final EditText editText = view.findViewById(R.id.edit_send_text);
                final ToggleButton toggleButton = view.findViewById(R.id.bt_send_text);
                toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (!TextUtils.isEmpty(editText.getText())) {
                                byte[] data = editText.getText().toString().getBytes();
                                send(data);
                            } else {
                                Toast.makeText(getContext(), "发送不能为空", Toast.LENGTH_SHORT).show();
                                toggleButton.setChecked(false);
                            }
                        } else {
                            udpServer.udpServerClose();
                        }
                    }
                });
                break;
            case "图片":
                view = inflater.inflate(R.layout.fragment_send_pic, container, false);
                Button selectPic = view.findViewById(R.id.bt_select_pic);
                final Button sendPic = view.findViewById(R.id.bt_send_pic);
                selectPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                sendPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        byte[] data = editText.getText().toString().getBytes();
//                        send(data);
                    }
                });
                break;
            case "音频":
                view = inflater.inflate(R.layout.fragment_send_aud, container, false);
                Button sendAud = view.findViewById(R.id.bt_send_aud);
                sendAud.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        byte[] data = editText.getText().toString().getBytes();
//                        send(data);
                    }
                });
                break;
            default:
                break;
        }

        return view;
    }

    private void send(byte[] data) {
        baseMessage.setDataLength(data.length);
        baseMessage.setData(data);
        PackEmergencyProtocol<BaseMessage> packEmergencyProtocol = new PackEmergencyProtocol<>();
        emergencyProtocol = packEmergencyProtocol.packPackEmergencyProtocol(baseMessage);
        byte[] sendBytes = emergencyProtocol.getEmergencyProtocolByte();
        Log.d(TAG, "send:" + emergencyProtocol.toString());
        Log.d(TAG, "data length: " + sendBytes.length);
        udpServer.start(sendBytes);// 异步
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onBaseFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBaseFragmentInteractionListener) {
            mListener = (OnBaseFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBaseFragmentInteractionListener");
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
    public interface OnBaseFragmentInteractionListener {
        // TODO: Update argument type and name
        void onBaseFragmentInteraction(Uri uri);
    }

    static class UdpTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... bytes) {
            udpServer.start(bytes[0]);
            return null;
        }
    }

}
