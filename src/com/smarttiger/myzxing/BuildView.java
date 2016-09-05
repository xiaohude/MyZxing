package com.smarttiger.myzxing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BuildView extends Activity implements OnClickListener {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFffffff;
    
	private TextView scanningText;
	private TextView buildText;
	private Button formatButton;
	private String format;
	private EditText editText;
	private ImageView imageView;
	private String text = "";
	private TextView logText;
	
	private BarcodeFormat[] formats = {BarcodeFormat.QR_CODE, BarcodeFormat.PDF_417, BarcodeFormat.CODE_128};
	private int formatId = 0;
	
	private ClipboardManager myClipboard;//剪贴板
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buildview);
		

		myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		
		scanningText = (TextView) findViewById(R.id.scanning_text);
		buildText = (TextView) findViewById(R.id.build_text);
		formatButton = (Button) findViewById(R.id.format_button);
		scanningText.setOnClickListener(this);
		buildText.setOnClickListener(this);
		formatButton.setOnClickListener(this);
		editText = (EditText) findViewById(R.id.edit_text);
		logText = (TextView) findViewById(R.id.log_text);
		imageView = (ImageView) findViewById(R.id.image_view);
		imageView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
				saveBitmap(bitmap);
				return false;
			}
		});

		paste();
		buildQrCode();
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int id = view.getId();
		if(id == R.id.scanning_text) {
			Intent intent = new Intent(this, ScanningView.class);
			startActivity(intent);
		} else if (id == R.id.build_text) {
			text = editText.getText().toString();
			buildQrCode();
		} else if (id == R.id.format_button) {
//			text = editText.getText().toString();
//			buildQrCode();
			
			formatId = (formatId+1) % 3;
			formatButton.setText(formats[formatId].toString());
		}
	}
	
	
	private void buildQrCode () {
		try {

			if(isCODE_128HasCN())
				return;
			Bitmap mQrBitmap = encodeAsBitmap();
			if(mQrBitmap != null){
				imageView.setImageBitmap(mQrBitmap);
				logText.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
				if(isCODE_128HasCN())
					System.out.println("-----------mQrBitmap == null----");
			}
			else
				if(isCODE_128HasCN())
					System.out.println("-----------mQrBitmap == null----");
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			System.out.println("---------------123456----------------");
			e.printStackTrace();
		}
	}

	private Bitmap encodeAsBitmap() throws WriterException {
        if (TextUtils.isEmpty(text)) {
        	showLog("字符串为空");
            return null;
        }
        BarcodeFormat format = BarcodeFormat.valueOf(BarcodeFormat.QR_CODE.toString());
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(text);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        int mScaleImageHeight = 480;
        BitMatrix result;
        try {
        	System.out.println("text========="+text);
            result = new MultiFormatWriter().encode(text, formats[formatId], mScaleImageHeight,
            		mScaleImageHeight,hints);
        	System.out.println("result========="+result);
        } catch (IllegalArgumentException iae) {
        	isCODE_128HasCN();
            return null;
        }
        
        BitMatrix resMatrix = result;
        int width = resMatrix.getWidth();
        int height = resMatrix.getHeight();
        int[] pixels = new int[width * height];
        for(int y = 0;y<height;y++){
			for(int x = 0;x<width;x++)
			{
				if(resMatrix.get(x, y))
					pixels[y * width + x] = BLACK;
				else //可以不填白色，就是透明了，如果在黑色背景下就看不到了。
					pixels[y * width + x] = WHITE;
			}
		}

        
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        Matrix matrix = new Matrix();  
        matrix.postScale(mScaleImageHeight/width,mScaleImageHeight/height); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true); 
        return resizeBmp;
    }
	
	//获取当前字符串编码格式是否为UTF-8格式的。
	private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
	
	
	//直接粘贴剪贴板内容
	private void paste(){
		ClipData data = myClipboard.getPrimaryClip();
		if(data == null)
			return;
		ClipData.Item item = data.getItemAt(0);
		text = item.getText().toString();
		editText.setText(text);
		
		
		//清空剪贴板
//		data = ClipData.newPlainText("", "");
//		myClipboard.setPrimaryClip(data);
	}
	

	private  String HEAD_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/MyZxing/";
	//记得加权限
	private void saveBitmap(Bitmap bm) {
		File headFile = new File(HEAD_DIR);
		if(!headFile.exists())
			headFile.mkdir();
		
		File f = new File(HEAD_DIR, "/myQRcode.png");
		if (f.exists()) {
			f.delete();
		}
		try {
			f.createNewFile();
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			Toast.makeText(this, "二维码已保存到"+f.getPath(), 0).show();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void showLog(String text) {
		logText.setVisibility(View.VISIBLE);
		imageView.setVisibility(View.GONE);
		logText.setText(text);
	}
	
	//已选条形码清空下不能选择中文字符
	private boolean isCODE_128HasCN() {
		System.out.println("------------isCODE_128HasCN000-");
		if(formatId == 2) {
			Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]+");
			Matcher matcher = pattern.matcher(text);
			if(matcher.matches()) {
				showLog("中文无法生成条形码");
				return true;
			}
		}
		return false;
		
	}
}
