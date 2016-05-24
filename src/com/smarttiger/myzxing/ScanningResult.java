package com.smarttiger.myzxing;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.smarttiger.myzxing.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class ScanningResult extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanningresult);
        String intentS=getIntent().getStringExtra("code");
        System.out.println("intentS==="+intentS);
        if(intentS==null)
        	return;
        String result = autoDecode(intentS);
//        try {
        	
//        	result = result + "原字符串====\n" + intentS ;
//        	result = result + "\n\nISO解码为UTF-8====\n" + new String(intentS.getBytes("ISO-8859-1"),"UTF-8"); 
//        	result = result + "\n\nISO解码为GBK====\n" + new String(intentS.getBytes("ISO-8859-1"),"GBK"); 
        	
//        	result = result + "\n\n测试超链接====\n"
//        			+ Html.fromHtml("<a href=\""+"http://www.baidu.com"+"\">"+"《XXX条款和服务协议》"+"</a> ");
//        	
//        	result = result + "\n\n测试超链接====\n"
//                	+ Html.fromHtml("<font color=blue><u>" + "http://www.baidu.com"
//							+ "</u></font>");
        	
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
        TextView txt=(TextView)findViewById(R.id.txtcode);
        txt.setTextColor(Color.rgb(127, 127, 127));
        txt.setTextSize((float) 16.0);
        txt.setText(result);

        
//        
//        TextView myTextView = (TextView) this.findViewById(R.id.txtcode);  
//        SpannableString sp = new SpannableString("这句话中有百度超链接,有高亮显示，这样，或者这样，还有斜体.");             
//        sp.setSpan(new URLSpan("http://www.baidu.com"), 5, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);          
//        sp.setSpan(new BackgroundColorSpan(Color.RED), 17 ,19,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);        
//        sp.setSpan(new ForegroundColorSpan(Color.YELLOW),20,24,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);      
//        sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 27, 29, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);           
//        //SpannableString对象设置给TextView          
//        myTextView.setText(sp);          
//         //设置TextView可点击          
//        myTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
    
    public void onBackToMain(View view)
    {
    	startActivity(new Intent(this, ScanningView.class));
    	finish();
    }
    
    //自动判断是用什么格式来解码显示字符串。
    private String autoDecode(String intentS)
    {  	
    	//参考代码：http://www.oschina.net/code/snippet_255711_9383
    	//http://bbs.csdn.net/topics/350053404
    	String result = "";
		try {
			byte b[] = intentS.getBytes("ISO-8859-1");
	    	for (int i = 0; i < b.length; i++)                 
	            if  (b[i] == 63) //不需要转码
	            {
	            	result = intentS ;
	            	return result;
	            }             
	    	
	    	Pattern p = Pattern.compile("^(?:[\\x00-\\x7f]|[\\xe0-\\xef][\\x80-\\xbf]{2})+$");  
	        Matcher m = p.matcher(intentS);  
	        if (m.find())//ISO解码为UTF-8               	        
	        	result = new String(b , "UTF-8");	         
	        else//ISO解码为GBK	        
	        	result = new String(b , "GBK");     	
	        				    	
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}			
		return result;	
    }
  
  
}



