package com.summerrc.com.video;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DialogActivity extends Activity implements
		DialogInterface.OnClickListener {
	private Button setip = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		setip = (Button) findViewById(R.id.setip);
		setip.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onShowPromptDialog(v);

			}
		});
	}

	public void onShowPromptDialog(View v) {
		// inflate需要显示到Dialog里的View对象
		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.log, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("IP");
		builder.setIcon(R.drawable.ic_launcher);
		// 之前inflate的View 放到dialog中
		builder.setView(view);
		builder.setPositiveButton("confirm", this);
		builder.setNegativeButton("cancel", this);
		builder.create().show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == android.app.Dialog.BUTTON_POSITIVE) {

			AlertDialog ad = (AlertDialog) dialog;
			EditText t = (EditText) ad.findViewById(R.id.editText_prompt);

			String ip = t.getText().toString();
			StreamTool.createFile(ip);
			Toast.makeText(this, t.getText().toString(), Toast.LENGTH_LONG)
					.show();
		}

	}

}
