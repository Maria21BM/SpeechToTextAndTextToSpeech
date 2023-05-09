package com.example.speechtotext

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.speechtotext.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var speechRecognizer: SpeechRecognizer? = null
    private val REQUEST_CODE_SPEECH_INOUT = 100
    // Text to Speech
    lateinit var tts: TextToSpeech

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMicSpeak.setOnClickListener {
            speak()
        }

//        // Speech to text
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            checkPermissions()
//        }
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
//        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        speechRecognizerIntent.putExtra(
//            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//        )
//        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
//
//        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
//
//            override fun onReadyForSpeech(params: Bundle?) {
//                showToast("Ready for speech")
//            }
//
//            override fun onBeginningOfSpeech() {
//                binding.speechToText.setText("")
//                binding.speechToText.hint = "Listening..."
//            }
//
//            override fun onRmsChanged(p0: Float) {
//            }
//
//            override fun onBufferReceived(p0: ByteArray?) {
//            }
//
//            override fun onEndOfSpeech() {
//                binding.speechToText.hint = "Speech Ended."
//                showToast("Speech Ended.")
//            }
//
//            override fun onError(error: Int) {
//                showToast("Error: $error")
//            }
//
//            override fun onResults(bundle: Bundle?) {
//                val data = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//                if (data != null && data.isNotEmpty()) {
//                    val recognizedText = data[0]
//                    showToast("Recognized: $recognizedText")
//                    binding.speechToText.setText(recognizedText)
//                }
//            }
//
//            override fun onPartialResults(p0: Bundle?) {
//            }
//
//            override fun onEvent(p0: Int, p1: Bundle?) {
//            }
//
//        })
//
//        binding.micBtn.setOnTouchListener { _, motionEvent ->
//            when (motionEvent.action) {
//                MotionEvent.ACTION_UP -> {
//                    speechRecognizer?.stopListening()
//                    binding.micBtn.setImageResource(R.drawable.ic_mic_off)
//                }
//                MotionEvent.ACTION_DOWN -> {
//                    binding.micBtn.setImageResource(R.drawable.ic_mic)
//                    speechRecognizer?.startListening(speechRecognizerIntent)
//                }
//            }
//            false
//        }

        // Read text
        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.UK
            }
        })

        // speak btn click
        binding.btnSpeak.setOnClickListener {
            // get text from edit text
            val toSpeak = binding.textToSpeech.text.toString()
            if (toSpeak == "") {
                // if there is no text in edit text
                Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show()
            } else {
                // if there is text in edit text
                Toast.makeText(this, toSpeak, Toast.LENGTH_SHORT).show()
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
            }
        }

        // stop speaking btn
        binding.btnStop.setOnClickListener {
            if (tts.isSpeaking) {
                // if speaking then stop
                tts.stop()
                // tts.shutdown()
            } else {
                // if not speaking
                Toast.makeText(this, "Not speaking", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun speak() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something")
        
        try{
            // if there is no error show Speech To Text dialog
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INOUT)
        }catch(e:java.lang.Exception){
            // if there is any error get error message and show in toast
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }
    // receive voice input
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_SPEECH_INOUT -> {
                if(resultCode == Activity.RESULT_OK && null != data){
                    // get text from result
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val speechText = result?.get(0) ?: ""
                    // set the text to textView
                    binding.speechToText.text = Editable.Factory.getInstance().newEditable(speechText)
                }
            }
        }
    }





    

    override fun onPause() {
        if (tts.isSpeaking) {
            // if speaking then stop
            tts.stop()
            // tts.shutdown()
        }
        super.onPause()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        speechRecognizer!!.destroy()
//    }
//
//    private fun checkPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.RECORD_AUDIO), RecordAudioRequestCode
//            )
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == RecordAudioRequestCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            showToast("Permission Granted")
//        } else {
//            showToast("Permission Denied")
//        }
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
//
//    companion object {
//        const val RecordAudioRequestCode = 1
//    }

}


