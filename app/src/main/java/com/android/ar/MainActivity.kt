package com.android.ar

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.android.ar.databinding.ActivityMainBinding
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import java.util.concurrent.CompletableFuture


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding


    private var arFragment: ArFragment? = null
    private var arSession: Session? = null
    private var modelRenderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get reference to ArFragment
        // Get reference to ArFragment
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as ArFragment?

        // Set up ArFragment
        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
            if (modelRenderable == null) {
                return@setOnTapArPlaneListener
            }

            // Create an anchor at the hit location
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment!!.arSceneView.scene)

            // Create a TransformableNode and add it to the anchor
            val transformableNode =
                TransformableNode(arFragment!!.transformationSystem)
            transformableNode.setParent(anchorNode)
            transformableNode.renderable = modelRenderable
            transformableNode.select()
        }

        // Load 3D model asynchronously

        // Load 3D model asynchronously
        val modelRenderableFuture: CompletableFuture<ModelRenderable> = ModelRenderable.builder()
            .setSource(this, R.raw.angle_grinder)
            .build()
        modelRenderableFuture.thenAccept { renderable -> modelRenderable = renderable }

    }

    override fun onResume() {
        super.onResume()

        // Resume ARCore session

        // Resume ARCore session
        if (arSession == null) {
            arSession = Session(this)
        }
        try {
            arFragment!!.arSceneView.arFrame ?: return
            if (arSession != null) {
                arSession!!.resume()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onPause() {
        super.onPause()
        // Pause ARCore session
        if (arSession != null) {
            arSession!!.pause()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}