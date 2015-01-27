package com.example.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ItemActivity extends ActionBarActivity {

    private EditText text_title, text_content;
    private TextView text_alarm;

    // 啟動功能用的請求代碼
    private static final int START_CAMERA = 0;
    private static final int START_RECORD = 1;
    private static final int START_LOCATION = 2;
    private static final int START_ALARM = 3;
    private static final int START_COLOR = 4;

    // 記事物件
    private Item item;

    private String fileName;
    private ImageView picture;

    private AlarmManager alarmManager;
    private PendingIntent operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        processViews();

        Intent intent = getIntent();
        String action = intent.getAction();

        // 如果是修改記事
        if (action.equals(".EDIT_ITEM")) {
            item = (Item) intent.getExtras().getSerializable(".Item");
            // 接收與設定記事標題
            text_title.setText(item.getTitle());
            text_content.setText(item.getContent());
        }
        // 新增記事
        else {
            item = new Item();
        }
    }

    private void processViews() {
        text_title = (EditText) findViewById(R.id.text_title);
        text_content = (EditText) findViewById(R.id.text_content);
        text_alarm = (TextView) findViewById(R.id.text_alarm);

        picture = (ImageView) findViewById(R.id.picture);

//        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent intent = new Intent(".AlarmReceiver");
//        operation = PendingIntent.getBroadcast(this, (int) item.getId(),intent, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 如果有檔案名稱
        if (item.getFileName() != null && item.getFileName().length() > 0) {
            // 照片檔案物件
            File file = configFileName("P", ".jpg");

            // 如果照片檔案存在
            if (file.exists()) {
                // 顯示照片元件
                picture.setVisibility(View.VISIBLE);
                // 設定照片
                FileUtil.fileToImageView(file.getAbsolutePath(), picture);
            }
        }

        if (item.getAlarmTime() > 0) {
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            text_alarm.setText(date_format.format(new Date(item.getAlarmTime())));
            text_alarm.setVisibility(View.VISIBLE);
        } else {
            text_alarm.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickFunction(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.imgbtn_camera:
                // 啟動相機元件用的Intent物件
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // 照片檔案名稱
                File pictureFile = configFileName("P", ".jpg");
                Uri uri = Uri.fromFile(pictureFile);
                // 設定檔案名稱
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 啟動相機元件
                startActivityForResult(intentCamera, START_CAMERA);
                break;
            case R.id.imgbtn_record:
                // 錄音檔案名稱
                final File recordFile = configFileName("R", ".mp3");

                if (recordFile.exists()) {
                    // 詢問播放還是重新錄製的對話框
                    AlertDialog.Builder d = new AlertDialog.Builder(this);

                    d.setTitle(R.string.title_record).setCancelable(true);
                    d.setPositiveButton(R.string.record_play,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 播放
                                    Intent playIntent = new Intent(
                                            ItemActivity.this, PlayActivity.class);
                                    playIntent.putExtra("fileName",
                                            recordFile.getAbsolutePath());
                                    startActivity(playIntent);
                                }
                            });
                    d.setNegativeButton(R.string.record_new,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    goToRecord(recordFile);
                                }
                            });

                    // 顯示對話框
                    d.show();
                }
                else {
                    goToRecord(recordFile);
                }
                break;
            case R.id.imgbtn_location:
                break;
            case R.id.imgbtn_alarm:
                startActivityForResult(new Intent(this, AlarmActivity.class), START_ALARM);
                break;
            case R.id.imgbtn_color:
                startActivityForResult(new Intent(this, ColorActivity.class), START_COLOR);
                break;
        }
     }

    private void goToRecord(File recordFile) {
        // 錄音
        Intent recordIntent = new Intent(this, RecordActivity.class);
        recordIntent.putExtra("fileName", recordFile.getAbsolutePath());
        startActivityForResult(recordIntent, START_RECORD);
    }

    private File configFileName(String prefix, String extension) {
        // 如果記事資料已經有檔案名稱
        if (item.getFileName() != null && item.getFileName().length() > 0) {
            fileName = item.getFileName();
        }
        // 產生檔案名稱
        else {
            fileName = FileUtil.getUniqueFileName();
        }

        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
            prefix + fileName + extension);
    }

    public void onSubmit(View view) {
        if(view.getId() == R.id.btn_ok) {
            String titleText = text_title.getText().toString();
            String contentText = text_content.getText().toString();

            item.setTitle(titleText);
            item.setContent(contentText);

            // 如果是修改記事
            if (getIntent().getAction().equals(".EDIT_ITEM")) {
                item.setLastModify(new Date().getTime());
            }
            // 新增記事
            else {
                item.setDatetime(new Date().getTime());
                // 建立SharedPreferences物件
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                // 讀取設定的預設顏色
                int color = sharedPreferences.getInt("DEFAULT_COLOR", -1);
                item.setColor(getColors(color));
            }

            Intent result = getIntent();
            result.putExtra(".Item", item);

            if (item.getAlarmTime() > 0) {
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(this, AlarmReceiver.class);
                operation = PendingIntent.getBroadcast(this, (int) item.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, item.getAlarmTime(), operation);
                Toast.makeText(this, "set alarm success", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, AlarmReceiver.class);
                operation = PendingIntent.getBroadcast(this, (int) item.getId(), intent, 0);
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(operation);
            }

            setResult(Activity.RESULT_OK, result);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case START_CAMERA:
                    item.setFileName(fileName);
                    break;
                case START_RECORD:
                    item.setFileName(fileName);
                    break;
                case START_LOCATION:
                    break;
                case START_ALARM:
                    String picked_datetime = data.getStringExtra("pickedDatetime");
                    if (picked_datetime == null || picked_datetime.length() == 0) {
                        item.setAlarmTime(0);
                        break;
                    }
                    picked_datetime = picked_datetime + ":00";

                    SimpleDateFormat date_format;
                    date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date d = date_format.parse(picked_datetime);
                        item.setAlarmTime(d.getTime());
                    } catch (ParseException e) {
                        item.setAlarmTime(0);
                        e.printStackTrace();
                    }
                    break;
                // 設定顏色
                case START_COLOR:
                    int colorId = data.getIntExtra("colorId", Colors.LIGHTGREY.parseColor());
                    item.setColor(getColors(colorId));
                    break;
            }
        }
    }

    public static Colors getColors(int color) {
        Colors result = Colors.LIGHTGREY;

        if (color == Colors.BLUE.parseColor()) {
            result = Colors.BLUE;
        }
        else if (color == Colors.PURPLE.parseColor()) {
            result = Colors.PURPLE;
        }
        else if (color == Colors.GREEN.parseColor()) {
            result = Colors.GREEN;
        }
        else if (color == Colors.ORANGE.parseColor()) {
            result = Colors.ORANGE;
        }
        else if (color == Colors.RED.parseColor()) {
            result = Colors.RED;
        }

        return result;
    }
}
