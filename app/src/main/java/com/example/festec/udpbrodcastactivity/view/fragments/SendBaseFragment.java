package com.example.festec.udpbrodcastactivity.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.festec.udpbrodcastactivity.R;
import com.example.festec.udpbrodcastactivity.module.GlobalValues;
import com.example.festec.udpbrodcastactivity.module.message.BaseMessage;
import com.example.festec.udpbrodcastactivity.module.protocol.EmergencyProtocol;
import com.example.festec.udpbrodcastactivity.module.protocol.PackEmergencyProtocol;
import com.example.festec.udpbrodcastactivity.module.udp.UdpServer;
import com.example.festec.udpbrodcastactivity.module.utils.ByteUtils;
import com.example.festec.udpbrodcastactivity.module.utils.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.LinkedList;

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
    private static final int RC_CHOOSE_PHOTO = 686;


    private static UdpServer udpServer;

    private EmergencyProtocol<BaseMessage> emergencyProtocol;
    private BaseMessage baseMessage;

    // TODO: Rename and change types of parameters
    private String broadcastType;
    private String broadcastLevel;
    private String dataType;

    byte[] data;

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
        baseMessage = new BaseMessage(broadcastType, broadcastLevel, dataType);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        udpServer = new UdpServer(GlobalValues.checkedPort);

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
                                data = editText.getText().toString().getBytes();
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
                final Button selectPic = view.findViewById(R.id.bt_select_pic);
                final ToggleButton sendPic = view.findViewById(R.id.bt_send_pic);
                selectPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosePhoto();
                    }
                });
                sendPic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (data != null) {
                                send(data);
                            } else {
                                Toast.makeText(getContext(), "请选择图片", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            udpServer.udpServerClose();
                        }
                    }
                });
                break;
            case "音频":
                view = inflater.inflate(R.layout.fragment_send_aud, container, false);
                ToggleButton sendAud = view.findViewById(R.id.bt_send_aud);
                final boolean[] isRecording = {false};
                sendAud.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            isRecording[0] = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    initAudio();
                                    audioRec.startRecording();
                                    while (isRecording[0]) {
                                        int length = audioRec.read(audBuffer, 0, minBufferSize);
                                        if (length > 0) {
                                            send(audBuffer, length);
                                        }
                                    }
                                }
                            }).start();
                        } else {
                            isRecording[0] = false;
                            audioRec.stop();
                            audioRec.release();
                            udpServer.udpServerClose();
                        }
                    }
                });

                break;
            default:
                break;
        }
        return view;
    }

    /**
     * 打开系统相册
     */
    private void choosePhoto() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, RC_CHOOSE_PHOTO);
    }

    private int minBufferSize;
    private AudioRecord audioRec;
    private byte[] audBuffer;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initAudio() {
        //播放的采样频率 和录制的采样频率一样
        int sampleRate = 44100;
        //和录制的一样的
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        //录音用输入单声道  播放用输出单声道
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;

        minBufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        Log.d(TAG, "****RecordMinBufferSize = " + minBufferSize);
        audioRec = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize * 4);
        audBuffer = new byte[minBufferSize * 4];

        if (audioRec == null) {
            return;
        }
        //声学回声消除器 AcousticEchoCanceler 消除了从远程捕捉到音频信号上的信号的作用
        if (AcousticEchoCanceler.isAvailable()) {
            AcousticEchoCanceler aec = AcousticEchoCanceler.create(audioRec.getAudioSessionId());
            if (aec != null) {
                aec.setEnabled(true);
            }
        }

        //自动增益控制 AutomaticGainControl 自动恢复正常捕获的信号输出
        if (AutomaticGainControl.isAvailable()) {
            AutomaticGainControl agc = AutomaticGainControl.create(audioRec.getAudioSessionId());
            if (agc != null) {
                agc.setEnabled(true);
            }
        }

        //噪声抑制器 NoiseSuppressor 可以消除被捕获信号的背景噪音
        if (NoiseSuppressor.isAvailable()) {
            NoiseSuppressor nc = NoiseSuppressor.create(audioRec.getAudioSessionId());
            if (nc != null) {
                nc.setEnabled(true);
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case RC_CHOOSE_PHOTO:
                Uri uri = intent.getData();
                String filePath = FileUtil.getFilePathByUri(getContext(), uri);
                if (!TextUtils.isEmpty(filePath)) {
                    data = image2Bytes(filePath);
                }
//                if (!TextUtils.isEmpty(filePath)) {
//                    RequestOptions requestOptions1 = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
//                    //将照片显示在 ivImage上
//                    Glide.with(this).load(filePath).apply(requestOptions1).into(img);
//                }
                break;

            default:
                break;
        }
    }


    /**
     * 根据图片路径，把图片转为byte数组
     * @param path  图片路径
     * @return      byte[]
     */
    private byte[] image2Bytes(String path)
    {
        FileInputStream fin;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            fin = new FileInputStream(new File(path));
            byte[] buf = new byte[1024];
            int count = 0;
            while ((count = fin.read(buf)) > 0) {
                baos.write(buf, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }


    private void send(byte[] data) {
        baseMessage.setDataLength(data.length);
        baseMessage.setData(data);
        PackEmergencyProtocol<BaseMessage> packEmergencyProtocol = new PackEmergencyProtocol<>();
        emergencyProtocol = packEmergencyProtocol.packPackEmergencyProtocol(baseMessage);
        byte[] sendBytes = emergencyProtocol.getEmergencyProtocolByte();
//        Log.d(TAG, "send:" + emergencyProtocol.toString());
//        Log.d(TAG, "data length: " + sendBytes.length);
        if (sendBytes.length > 4096) {
            Toast.makeText(getContext(), "内容过大，请重新选择", Toast.LENGTH_SHORT).show();
            return;
        }
        udpServer.start(sendBytes);// 异步
    }

    private void send(byte[] data, int dataLength) {
        baseMessage.setDataLength(dataLength);
        baseMessage.setData(ByteUtils.splice(data, 0, dataLength, 0));
        PackEmergencyProtocol<BaseMessage> packEmergencyProtocol = new PackEmergencyProtocol<>();
        emergencyProtocol = packEmergencyProtocol.packPackEmergencyProtocol(baseMessage);
        byte[] sendBytes = emergencyProtocol.getEmergencyProtocolByte();
//        Log.d(TAG, "send:" + emergencyProtocol.getCrc32());
//        Log.d(TAG, "data length: " + sendBytes.length);
        if (sendBytes.length > 4096) {
            Toast.makeText(getContext(), "内容过大，请重新选择", Toast.LENGTH_SHORT).show();
            return;
        }
        udpServer.startNow(sendBytes);// 异步
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
        udpServer.udpServerClose();
        mListener = null;
        super.onDetach();
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
