package com.example.a7minuteworkout

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.a7minuteworkout.databinding.ActivityExerciseBinding
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {


    private var restTimer: CountDownTimer? =
        null
    private var restProgress =
        0

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseTimerDuration:Long = 30

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private  var tts: TextToSpeech? = null
    private  var player : MediaPlayer? = null

    private var binding:ActivityExerciseBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExerciseBinding.inflate(layoutInflater)

        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        tts = TextToSpeech(this,this)
        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }

        exerciseList = Constants.defaultExerciseList()

        setupRestView()
    }



    private fun setupRestView() {



        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingLable?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE

        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        binding?.tvUpcomingExerciseName?.text =
            exerciseList!![currentExercisePosition + 1].getName()


        setRestProgressBar()
    }


    private fun setRestProgressBar() {

        binding?.progressBar?.progress = restProgress // Sets the current progress to the specified value.



        restTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++ // It is increased by 1
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text =
                    (10 - restProgress).toString()
            }

            override fun onFinish() {
                // TODO(Step 8 - Increasing the current position of the exercise after rest view.)
                // START
                currentExercisePosition++
                setupExerciseView()
            }
        }.start()
    }

    private fun setupExerciseView() {

        // Here according to the view make it visible as this is Exercise View so exercise view is visible and rest view is not.
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.tvUpcomingLable?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE

        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())


        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        // END
        setExerciseProgressBar()

    }

    private fun setExerciseProgressBar() {

        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = exerciseTimerDuration.toInt() - exerciseProgress
                binding?.tvTimerExercise?.text = (exerciseTimerDuration.toInt() - exerciseProgress).toString()
            }

            override fun onFinish() {

                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    setupRestView()
                } else {

                    Toast.makeText(
                        this@ExerciseActivity,
                        "Congratulations! You have completed the 7 minutes workout.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // END
            }
        }.start()

    }
    // END



    public override fun onDestroy() {
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        if (tts != null){
            tts?.stop()
            tts?.shutdown()
        }


        super.onDestroy()
        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            var result = tts!!.setLanguage(Locale.ENGLISH)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The Language specified is not supported")
            }
        }else{
            Log.e("TTS", "Initialization Failed!")
        }
    }

    private fun speakOut(text : String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

}