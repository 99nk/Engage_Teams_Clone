const myVideoGrid = document.getElementById("my-video-grid");
const myVideoReview = document.createElement("video");
myVideoReview.muted = true;

let myVideoStream;
navigator.mediaDevices
  .getUserMedia({
    audio: true,
    video: true,
  })
  .then((stream) => {
    myVideoStream = stream;
    addVideoStream(myVideoReview, stream);
  });

const addVideoStream = (video, stream) => {
  video.srcObject = stream;
  video.addEventListener("loadedmetadata", () => {
    video.play();
    myVideoGrid.append(video);
  });
};

const muteButtonR = document.querySelector("#muteButtonR");
const stopVideoR = document.querySelector("#stopVideoR");
  
muteButtonR.addEventListener("click", () => {
  const enabled = myVideoStream.getAudioTracks()[0].enabled;
  if (enabled) {
    myVideoStream.getAudioTracks()[0].enabled = false;
    html = `<i class="fas fa-microphone-slash"></i>`;
    muteButtonR.classList.toggle("background__red");
    muteButtonR.innerHTML = html;
    } 
    else 
    {
    myVideoStream.getAudioTracks()[0].enabled = true;
    html = `<i class="fas fa-microphone"></i>`;
    muteButtonR.classList.toggle("background__red");
    muteButtonR.innerHTML = html;
    }
});

stopVideoR.addEventListener("click", () => {
  const enabled = myVideoStream.getVideoTracks()[0].enabled;
  if (enabled) {
    myVideoStream.getVideoTracks()[0].enabled = false;
    html = `<i class="fas fa-video-slash"></i>`;
    stopVideoR.classList.toggle("background__red");
    stopVideoR.innerHTML = html;
  } else {
    myVideoStream.getVideoTracks()[0].enabled = true;
    html = `<i class="fas fa-video"></i>`;
    stopVideoR.classList.toggle("background__red");
    stopVideoR.innerHTML = html;
  }
});

document.getElementById( 'enter-room' ).addEventListener( 'click', ( e ) => {
  e.preventDefault();

  let name = document.querySelector( '#username' ).value;
  let checkVideo=myVideoStream.getVideoTracks()[0].enabled.toString();
  let checkAudio=myVideoStream.getAudioTracks()[0].enabled.toString();
  if (name) 
  {
      //remove error message, if any
      document.querySelector( '#err-msg-username' ).innerHTML = "";

      //save the user's name in sessionStorage
      sessionStorage.setItem( 'username', name );
      sessionStorage.setItem('video',checkVideo);
      sessionStorage.setItem('audio',checkAudio);
      //reload room
      //location.reload();
      //go to the meeting
      //String pre=window.location.toString();
      var obj=window.location.href;
      obj.toString();
      let str=obj;
      let p=str.substr(0,40);
      window.location.replace(p);
  }
  else {
    document.querySelector( '#err-msg-username' ).innerHTML = "Please input your name";
}
} );

