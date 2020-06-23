package com.pyo.safe_guard

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.SystemClock
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_googlemap.*
import org.jetbrains.anko.toast


class googlemap : AppCompatActivity(), OnMapReadyCallback {

    private val polyLineOptions=PolylineOptions().width(5f).color(Color.RED) // 이동경로를 그릴 선
    private lateinit var mMap: GoogleMap // 마커, 카메라 지정을 위한 구글 맵 객체
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient // 위치 요청 메소드 담고 있는 객체
    private lateinit var locationRequest:LocationRequest // 위치 요청할 때 넘겨주는 데이터에 관한 객체
    private lateinit var locationCallback:MyLocationCallBack // 위치 확인되고 호출되는 객체
    // 위치 정보를 얻기 위한 각종 초기화
    private fun locationInit(){
        // FusedLocationProviderClient 객체 생성
        // 이 객체의 메소드를 통해 위치 정보를 요청할 수 있음
        fusedLocationProviderClient=FusedLocationProviderClient(this)
        // 위치 갱신되면 호출되는 콜백 생성
        locationCallback=MyLocationCallBack()
        // (정보 요청할 때 넘겨줄 데이터)에 관한 객체 생성
        locationRequest=LocationRequest()
        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY // 가장 정확한 위치를 요청한다,
        locationRequest.interval=10000 // 위치를 갱신하는데 필요한 시간 <밀리초 기준>
        locationRequest.fastestInterval=5000 // 다른 앱에서 위치를 갱신했을 때 그 정보를 가져오는 시간 <밀리초 기준>
    }
    // 위치 정보를 찾고 나서 인스턴스화되는 클래스
    inner class MyLocationCallBack:LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            // lastLocation프로퍼티가 가리키는 객체 주소를 받는다.
            // 그 객체는 현재 경도와 위도를 프로퍼티로 갖는다.
            // 그러나 gps가 꺼져 있거나 위치를 찾을 수 없을 때는 lastLocation은 null을 가진다.
            val location = locationResult?.lastLocation
            //  gps가 켜져 있고 위치 정보를 찾을 수 있을 때 다음 함수를 호출한다. <?. : 안전한 호출>
            location?.run{
                // 현재 경도와 위도를 LatLng메소드로 설정한다.
                val latLng=LatLng(latitude,longitude)
                // 카메라를 이동한다.( 이동할 위치,줌 수치)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))
                // 마커를 추가한다.
                mMap.addMarker(MarkerOptions().position(latLng).title("Changed Location"))
                // polyLine에 좌표 추가
                polyLineOptions.add(latLng)
                // 선 그리기
                mMap.addPolyline(polyLineOptions)
            }
        }
    }
    // 이 메소드부터 프로그램 시작
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_googlemap)
        // 화면이 꺼지지 않게 하기
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 세로 모드로 화면 고정
        requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // 위치 정보를 얻기 위한 각종 초기화
        locationInit()
        // 프래그먼트 매니저로부터 SupportMapFragment프래그먼트를 얻는다. 이 프래그먼트는 지도를 준비하는 기능이 있다.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // 지도가 준비되면 알림을 받는다. (아마, get함수에서 다른 함수를 호출해서 처리하는 듯)
        mapFragment.getMapAsync(this)
    }
    // 프로그램이 켜졌을 때만 위치 정보를 요청한다
    override fun onResume(){
        super.onResume()
        // 사용자에게 gps키라고 알리기
        Toast.makeText(this,"이 앱은 GPS(위치)를 켜야 이용 가능합니다!", Toast.LENGTH_SHORT).show()
        // '앱이 gps사용'에 대한 권한이 있는지 체크
        // 거부됐으면 showPermissionInfoDialog(알림)메소드를 호출, 승인됐으면 addLocationListener(위치 요청)메소드를 호출
        permissionCheck(cancel={showPermissionInfoDialog()},
            ok={addLocationListener()})
    }
    // 프로그램이 중단되면 위치 요청을 삭제한다
    override fun onPause(){
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
    // 위치 요청 메소드
    @SuppressLint("MissingPermission")
    private fun addLocationListener(){
        // 위치 정보 요청
        // (정보 요청할 때 넘겨줄 데이터)에 관한 객체, 위치 갱신되면 호출되는 콜백, 특정 스레드 지정(별 일 없으니 null)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
    }
    /**
     * 사용 가능한 맵을 조작한다.
     * 지도를 사용할 준비가 되면(알림을 받으면) 이 함수가 호출된다.
     * 이 함수에서 마커(표시)나 선을 추가하거나 카메라를 이동할 수 있다.
     * 기본 마커는 호주 시드니 근처이다.
     * GooglePlay 서비스가 기기에 설치돼있지 않은 경우,
     * 사용자에게 SupportMapFragment 안에 GooglePlay 서비스를 설치하라는 메시지가 표시된다.
     * 이 함수는 사용자가 GooglePlay 서비스를 설치하고 앱으로 돌아온 후에만 실행된다.
     **/
    override fun onMapReady(googleMap: GoogleMap) {
        // googleMap객체 생성
        // googleMap을 이용하여 마커나 카메라,선 등을 조정하는 것이다
        mMap = googleMap
        // 서울을 기준으로 위도,경도 지정
        val SEOUL = LatLng(37.56, 126.97)
        // MarkerOptions에 위치 정보를 넣을 수 있음
        val markerOptions = MarkerOptions()
        markerOptions.position(SEOUL)
        markerOptions.title("서울")
        markerOptions.snippet("한국의 수도")
        // 마커 추가
        mMap.addMarker(markerOptions)
        // 카메라 서울로 이동
        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL))
        // 10만큼 줌인 하기
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10f))
        // P.s. onMapReady가 호출된 후에 위치 수정이 가능하다!!
    }
    /** 아래 내용은 사용자에게 권한 부여를 받고 처리하는 메소드이다. **/
    private val gps_request_code=1000 // gps에 관한 권한 요청 코드(번호)
    private fun permissionCheck(cancel:()->Unit,ok:()->Unit){
        // 앱에 GPS이용하는 권한이 없을 때
        // <앱을 처음 실행하거나, 사용자가 이전에 권한 허용을 안 했을 때 성립>
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){  // <PERMISSION_DENIED가 반환됨>
            // 이전에 사용자가 앱 권한을 허용하지 않았을 때 -> 왜 허용해야되는지 알람을 띄움
            // shouldShowRequestPermissionRationale메소드는 이전에 사용자가 앱 권한을 허용하지 않았을 때 ture를 반환함
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                cancel()
            }
            // 앱 처음 실행했을 때
            else
            // 권한 요청 알림
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),gps_request_code)        }
        // 앱에 권한이 허용됨
        else
            ok()
    }
    // 사용자가 권한 요청<허용,비허용>한 후에 이 메소드가 호출됨
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            // (gps 사용에 대한 사용자의 요청)일 때
            gps_request_code->{
                // 요청이 허용일 때
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    addLocationListener()
                // 요청이 비허용일 때
                else{
                    Toast.makeText(this, "권한 거부됌.", Toast.LENGTH_LONG).show()
                    finish() }
            }
        }
    }
    // 사용자가 이전에 권한을 거부했을 때 호출된다.
    private fun showPermissionInfoDialog(){
        alert("지도 정보를 얻으려면 위치 권한이 필수로 필요합니다",""){
            yesButton{
                // 권한 요청
                ActivityCompat.requestPermissions(this@googlemap,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),gps_request_code)
            }
            noButton { toast("권한 거부 됨")
                finish() }
        }.show()
    }
    /** 여기까지 권한에 대한 블럭이다. **/
}
