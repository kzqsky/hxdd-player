package com.edu.hxdd_player.activity;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.edu.hxdd_player.R;
import com.edu.hxdd_player.api.net.OkUtils;
import com.edu.hxdd_player.bean.media.Question;
import com.edu.hxdd_player.bean.media.QuestionOption;
import com.edu.hxdd_player.bean.parameters.GetChapter;
import com.edu.hxdd_player.callback.TimeCallBack;
import com.edu.hxdd_player.fragment.ExamFragment;
import com.edu.hxdd_player.utils.StartPlayerUtils;
import com.edu.hxdd_player.utils.TimeUtil;
import com.edu.hxdd_player.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExamTestActivity extends AppCompatActivity implements ExamFragment.ExamFragmentCallback {
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    TextView textView;
    TimeUtil timeUtil;
    GetChapter getChapter = new GetChapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hxdd_player_activity_exam_test);


        findViewById(R.id.hxdd_player_button).setOnClickListener(v -> {
            ExamFragment examFragment = ExamFragment.newInstance(buildQuestion());
            examFragment.show(getSupportFragmentManager(), "exam");
        });
        findViewById(R.id.hxdd_player_button2).setOnClickListener(v -> {
            ExamFragment examFragment = ExamFragment.newInstance(buildQuestionM());
            examFragment.show(getSupportFragmentManager(), "exam");
        });
        textView = findViewById(R.id.hxdd_player_text);
        textView.setTextColor(StartPlayerUtils.getColorPrimary());
        timeUtil = new TimeUtil();
        timeUtil.setCallback(new TimeUtil.TimeUtilCallback() {
            @Override
            public void time(long time) {
                textView.setText(time + "");
            }
        });
        findViewById(R.id.hxdd_player_button3).setOnClickListener(v -> {
            timeUtil.start();
        });
        findViewById(R.id.hxdd_player_button4).setOnClickListener(v -> {
            timeUtil.setTimeInterval(TimeUtil.ONE_HALF);
        });
        findViewById(R.id.hxdd_player_button5).setOnClickListener(v -> {
            timeUtil.setTimeInterval(TimeUtil.ONE_SEVEN_FIVE);
        });
        findViewById(R.id.hxdd_player_button6).setOnClickListener(v -> {
            timeUtil.setTimeInterval(TimeUtil.TWO);
        });
        findViewById(R.id.hxdd_player_button7).setOnClickListener(v -> {
            timeUtil.pause();
        });
        findViewById(R.id.hxdd_player_button8).setOnClickListener(v -> {
            timeUtil.resume();
        });
        findViewById(R.id.hxdd_player_button9).setOnClickListener(v -> {
            timeUtil.stop();
        });
        findViewById(R.id.hxdd_player_button10).setOnClickListener(v -> {
            timeUtil.setTimeInterval(TimeUtil.DEFAULT);
        });
        //获取播放参数
        findViewById(R.id.hxdd_player_button12).setOnClickListener(v -> {
            getParameters();
        });
        findViewById(R.id.hxdd_player_button11).setOnClickListener(v -> {

            getChapter.playByOrder = true;
            getChapter.playCanRepeat = true;
            getChapter.overMaxLearnHoursStop = true;
            getChapter.maxTimePerDay = 0;

            new StartPlayerUtils(this, getChapter)
                    .colorPrimary(getResources().getColor(R.color.alivc_green))
                    .videoPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/edu_video2/")
                    .downLoad(true)
                    .handout(false)
                    .cacheMode(false)
                    .callBackTime(1, new TimeCallBack() {
                        @Override
                        public void oneSecondCallback(PlayerActivity activity, long time, long currentTime, long duration, String currentCatalogID, String coursewareCode) {
//                            Log.e("test", time + "." + currentTime + ":" + duration + "--currentCatalogID:" + currentCatalogID + "--coursewareCode:" + coursewareCode);
//                            if (time == 10) {
//                                activity.setVideoRecord(true);
//                            }
//                            if (time >= 20) {
//                                activity.setVideoRecord(false);
//                            }

                        }
                    })
                    .play();

        });

        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                1);
    }

    private void getParameters() {
        getChapter = new GetChapter();
        //应为传递过来的数据bean
        getChapter.publicKey = "bea4fdfc732adbea8bbae096bbe3f492";
        getChapter.timestamp = "1700012389974";
        getChapter.businessLineCode = "ld_gk";
        getChapter.coursewareCode = "xzk_14658_jj";
        getChapter.courseCode = "00009";
        getChapter.catalogId = "";
        getChapter.clientCode = "202404";
        getChapter.userId = "681fbe7a992c42b88bc61d37d3d3c3ab";
        getChapter.userName = "18810561000";
        getChapter.validTime = "0";
        getChapter.lastTime = "0";
        getChapter.serverUrl = "https://cwstest.edu-edu.com:7443";
        getChapter.isQuestion = true;
        getChapter.hintPoint = 1;
        getChapter.drag = 0;
//        getChapter.logoUrl = "https://edu-apps.oss-cn-beijing.aliyuncs.com/test/123.jpg";
//        getChapter.logoPosition = 1;
//        getChapter.logoAlpha = 0.6f;
//        getChapter.logoWidth = 240;
//        getChapter.logoHeight = 72;
        getChapter.defaultQuality = "LD";
        getChapter.backUrl = "";

        OkHttpClient okHttpClient = OkUtils.getOkhttpBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
//        Gson gson = new Gson();
//        String requestBody = gson.toJson(data);
        final Request request = new Request.Builder()
                .url("https://cwstest.edu-edu.com:7443/client/clientKey")
                .get()
                .post(RequestBody.create(mediaType, "{\"businessLineCode\":\"ld_gk\",\n" +
                        "\"coursewareCode\":\"xzk_14658_jj\",\n" +
                        "\"courseCode\":\"00009\",\n" +
                        "\"catalogId\":\"\",\n" +
                        "\"clientCode\":\"202404\",\n" +
                        "\"userId\":\"681fbe7a992c42b88bc61d37d3d3c3ab\",\n" +
                        "\"userName\":\"18810561000\",\n" +
                        "\"maxTimePerDay\":\"0\",\n" +
                        "\"lastTime\":0,\n" +
                        "\"validTime\":0}"))
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showLong(ExamTestActivity.this, "获取播放参数失败:" + showErrorMessage(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {

                    String data = response.body().string();
                    if (!TextUtils.isEmpty(data)) {
                        try {
                            JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
                            String j = jsonObject.getAsJsonPrimitive("data").getAsString();
                            JsonObject json = new JsonParser().parse(j).getAsJsonObject();

                            getChapter.publicKey = json.get("public").getAsString();
                            getChapter.timestamp = json.get("timestamp").getAsString();
                            ToastUtils.showLong(ExamTestActivity.this, "获取播放参数成功！");
                        } catch (Exception e) {
                            ToastUtils.showLong(ExamTestActivity.this, "获取播放参数失败！" + e.getMessage());
                        }
                    }
                } else {
                    ToastUtils.showLong(ExamTestActivity.this, "获取播放参数失败！返回空");
                }
            }
        });
    }

    private String showErrorMessage(String error) {
        String s = "未知错误";
        if (error != null) {
            s = error;
            if (error.toLowerCase().equals("timeout")) {
                s = "网络异常，请检查网络";
            }
        }
        return s;
    }


    private String buildQuestion() {
        Question question = new Question();
        question.analysis = "试题解析";
        question.questionStem = "<p>这是个题干,是的犯法的阿斯顿发斯蒂芬打算过分过分的公司发的个人而人工蜂</p>";
        question.analysis = "<p>解析此题的真正答案是Two<img title=\"logo.png\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAANwAAABACAYAAABx/hm5AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NUVBOEVGNEJFNEJDMTFFOEIwQjNGRjMyMEEyQjlENTQiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NUVBOEVGNENFNEJDMTFFOEIwQjNGRjMyMEEyQjlENTQiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo1RUE4RUY0OUU0QkMxMUU4QjBCM0ZGMzIwQTJCOUQ1NCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo1RUE4RUY0QUU0QkMxMUU4QjBCM0ZGMzIwQTJCOUQ1NCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PosGV3AAAC34SURBVHja7F0JnI5V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\" width=\"220\" height=\"64\" /></p>";
        question.questionType = 1;
        question.optionList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            QuestionOption questionOption = new QuestionOption();
            questionOption.quesOption = "题目选项" + i;
            questionOption.quesValue = "" + (i + 1);
            if (i == 3) {
                questionOption.correct = true;
            } else {
                questionOption.correct = false;
            }
            question.optionList.add(questionOption);
        }

        return new Gson().toJson(question);
    }

    private String buildQuestionM() {
        Question question = new Question();
        question.analysis = "试题解析";
        question.questionStem = "<p>这是个题干,是的犯法的阿斯顿发斯蒂芬打算过分过分的公司发的个人而人工蜂</p>";
        question.analysis = "<p>解析此题的真正答案是Two<img title=\"logo.png\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAANwAAABACAYAAABx/hm5AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NUVBOEVGNEJFNEJDMTFFOEIwQjNGRjMyMEEyQjlENTQiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NUVBOEVGNENFNEJDMTFFOEIwQjNGRjMyMEEyQjlENTQiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo1RUE4RUY0OUU0QkMxMUU4QjBCM0ZGMzIwQTJCOUQ1NCIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo1RUE4RUY0QUU0QkMxMUU4QjBCM0ZGMzIwQTJCOUQ1NCIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PosGV3AAAC34SURBVHja7F0JnI5V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\" width=\"220\" height=\"64\" /></p>";
        question.questionType = 2;
        question.optionList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            QuestionOption questionOption = new QuestionOption();
            questionOption.quesOption = "题目选项" + i;
            questionOption.quesValue = "" + (i + 1);
            if (i == 3 || i == 1) {
                questionOption.correct = true;
            } else {
                questionOption.correct = false;
            }
            question.optionList.add(questionOption);
        }

        return new Gson().toJson(question);
    }

    @Override
    public void commit(Question question) {
        Toast.makeText(this, "确定提交---" + question.userAnswer, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void over(Question question) {
        Toast.makeText(this, "作答完毕-----" + question.isPass, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancel(Question question) {
        Toast.makeText(this, "作答取消", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        ToastUtils.clean();
        super.onDestroy();
    }
}
