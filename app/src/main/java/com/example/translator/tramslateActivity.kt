package com.ashencostha.mlkittranslations

import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.os.Bundle
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.speech.RecognizerIntent
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.translator.R
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class tramslateActivity : AppCompatActivity() {
    private lateinit var editTextLetters: EditText
    private lateinit var btnTranslate: Button
    private val REQUEST_CODE_SPEECH_INPUT = 100
    private lateinit var mic: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tramslate)
        editTextLetters = findViewById(R.id.editTextTranslate)
        btnTranslate = findViewById(R.id.btnTranslate)

        mic = findViewById(R.id.mic)
        mic.setOnClickListener {
            startSpeechRecognition("en-US")
//            startSpeechRecognition("mr-IN")

//            startSpeechRecognition("hi-IN")
        }
        btnTranslate.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(editTextLetters.text.toString())) {
                Toast.makeText(this, "No text allowed", Toast.LENGTH_SHORT).show()
            } else {
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
//                    .setSourceLanguage(TranslateLanguage.HINDI)
//                    .setSourceLanguage(TranslateLanguage.MARATHI)
                    .setTargetLanguage(TranslateLanguage.HINDI)
                    .build()
                val translator = Translation.getClient(options)
                val sourceText = editTextLetters.text.toString()
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Downloading the translation model...")
                progressDialog.setCancelable(false)
                progressDialog.show()
                translator.downloadModelIfNeeded()
                    .addOnSuccessListener {

                        progressDialog.dismiss()
                    }
                    .addOnFailureListener { progressDialog.dismiss() }
                val result = translator.translate(sourceText).addOnSuccessListener { s ->
                    Toast.makeText(
                        this,
                        s,
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    fun startSpeechRecognition(language: String) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,language)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            // Handle the exception if speech recognition is not supported on the device
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (results != null && results.isNotEmpty()) {
                val spokenText = results[0]
                editTextLetters.setText(spokenText.toString())
                // Handle the recognized text (spokenText)
            }
        }
    }

}
