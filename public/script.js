/*
Implementing the group video calling and chatting feature
Chats will be added to the firebase database which is synced with the andoid application.
*/
const socket = io("/");
const myVideo = document.createElement("video");
const videoGrid = document.getElementById("video-grid");
const backBtn = document.querySelector(".header__back");
const showChat = document.querySelector("#showChat");
myVideo.muted = true;

var firebaseConfig = 
{
  apiKey: "AIzaSyBIg8F6DX8k_QAQEx_5qeLI12zfrOSlPCc",
  authDomain: "ms-teams-a2222.firebaseapp.com",
  databaseURL: "https://ms-teams-a2222-default-rtdb.firebaseio.com",
  projectId: "ms-teams-a2222",
  storageBucket: "ms-teams-a2222.appspot.com",
  messagingSenderId: "1020789221491",
  appId: "1:1020789221491:web:9e7c8ce68c9ab535a26206",
  measurementId: "G-7SNYDL1GDN"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);

//Checking whether user is operating from android phone or web to accordingly set the layout of chat box
if(navigator.userAgent.match(/Android/i))
  {
    document.querySelector(".main__message_container").style.left = "0";
  }
  else
  {
    document.querySelector(".main__message_container").style.left = "70%";
  }

  backBtn.addEventListener("click", () => {
    document.querySelector(".main__left").style.display = "flex";
    document.querySelector(".main__left").style.flex = "1";
    document.querySelector(".main__right").style.display = "none";
    document.querySelector(".header__back").style.display = "none";
  });
  
  showChat.addEventListener("click", () => {
    document.querySelector(".main__right").style.display = "flex";
    document.querySelector(".main__right").style.flex = "1";
    document.querySelector(".main__left").style.display = "none";
    document.querySelector(".header__back").style.display = "block";
  });

const user = sessionStorage.getItem( 'username' );

var peer = new Peer(undefined, {
  path: "/peerjs",
  host: "/",
  port: "443",
});

let myVideoStream;
navigator.mediaDevices
  .getUserMedia({
    audio: true,
    video: true,
  })
  .then((stream) => {
    myVideoStream = stream;

    //Checking the video and audio toggle options from the preview activity retain user's preferences 
    let checkVideo=sessionStorage.getItem('video');
    if(checkVideo==="false")
    {
      myVideoStream.getVideoTracks()[0].enabled = false;
      html = `<i class="fas fa-video-slash"></i>`;
      stopVideo.classList.toggle("background__red");
      stopVideo.innerHTML = html;
    }
    const checkAudio=sessionStorage.getItem('audio');
    if(checkAudio==="false")
    {
      myVideoStream.getAudioTracks()[0].enabled = false;
      html = `<i class="fas fa-microphone-slash"></i>`;
      muteButton.classList.toggle("background__red");
      muteButton.innerHTML = html;
    }

    //adding users's video stream to the call
    addVideoStream(myVideo, stream);

    peer.on("call", (call) => {
      call.answer(stream);
      const video = document.createElement("video");
      call.on("stream", (userVideoStream) => {
        addVideoStream(video, userVideoStream);
      });
    });

    socket.on("user-connected", (userId) => {
      connectToNewUser(userId, stream);
    });
  });

const connectToNewUser = (userId, stream) => {
  const call = peer.call(userId, stream);
  const video = document.createElement("video");
  call.on("stream", (userVideoStream) => {
    addVideoStream(video, userVideoStream);
  });
};

peer.on("open", (id) => {
  socket.emit("join-room", ROOM_ID, id, user);
});

const addVideoStream = (video, stream) => {
  video.srcObject = stream;
  video.addEventListener("loadedmetadata", () => {
    video.play();
    videoGrid.append(video);
  });
};

let text = document.querySelector("#chat_message");
let send = document.getElementById("send");
let messages = document.querySelector(".messages");

send.addEventListener("click", (e) => {
  if (text.value.length !== 0) 
  {
    var currentdate=new Date();
    var message=text.value;
    var name=user;
    var date=" ";
    var time=" ";
    
    if(message!="" && name!="")
    {
      var msgData={
        "date":date,
        "name":name,
        "time":time,
        "message":message
      };

      var obj=window.location.href;
      obj.toString();
      let str=obj;
      let l=str.length;
      let ans=str.substr(35,l);
      let dis='Groups/'+ans+'/';
      
      var date=currentdate.getDate();
      var month=currentdate.getMonth()+1;
      var year=currentdate.getFullYear();
      var hours=currentdate.getHours()+1;
      var min=currentdate.getMinutes()+1;
      var sec=currentdate.getSeconds()+1;
      var time=hours.toString()+':'+min.toString()+':'+sec.toString();
      
      var randomKey;
      
      if(month<10)
      {
        if(date<10)
        {
          randomKey=year.toString()+'0'+month.toString()+'0'+date.toString();
        }
        else{
          randomKey=year.toString()+'0'+month.toString()+date.toString();
        }
      }
      else
      {
        if(date<10)
        {
          randomKey=year.toString()+month.toString()+'0'+date.toString();
        }
        else{
          randomKey=year.toString()+month.toString()+date.toString();
        }
      }
      randomKey=randomKey+time;
      firebase.database().ref(dis+randomKey).set(msgData,(error)=>{
        if(error)
        console.log("failed");
        else
        console.log("done");
      });
    }
    else{
      window.prompt("Incomplete message");
    }

    socket.emit("message", text.value);
    text.value = "";
  }
});

text.addEventListener("keydown", (e) => {
  if (e.key === "Enter" && text.value.length !== 0) {
    socket.emit("message", text.value);
    text.value = "";
  }
});

const inviteButton = document.querySelector("#inviteButton");
const muteButton = document.querySelector("#muteButton");
const stopVideo = document.querySelector("#stopVideo");
const cancelCall = document.querySelector("#cancelButton");
  
  cancelCall.addEventListener("click", ()=>{
    window.location.replace("https://test-video12.herokuapp.com/");
  });

muteButton.addEventListener("click", () => {
  const enabled = myVideoStream.getAudioTracks()[0].enabled;
  if (enabled) {
    myVideoStream.getAudioTracks()[0].enabled = false;
    html = `<i class="fas fa-microphone-slash"></i>`;
    muteButton.classList.toggle("background__red");
    muteButton.innerHTML = html;
  } else {
    myVideoStream.getAudioTracks()[0].enabled = true;
    html = `<i class="fas fa-microphone"></i>`;
    muteButton.classList.toggle("background__red");
    muteButton.innerHTML = html;
  }
});

stopVideo.addEventListener("click", () => {
  const enabled = myVideoStream.getVideoTracks()[0].enabled;
  if (enabled) {
    myVideoStream.getVideoTracks()[0].enabled = false;
    html = `<i class="fas fa-video-slash"></i>`;
    stopVideo.classList.toggle("background__red");
    stopVideo.innerHTML = html;
  } else {
    myVideoStream.getVideoTracks()[0].enabled = true;
    html = `<i class="fas fa-video"></i>`;
    stopVideo.classList.toggle("background__red");
    stopVideo.innerHTML = html;
  }
});

inviteButton.addEventListener("click", (e) => {
  prompt(
    "Copy this link and send it to people you want to meet with",
    window.location.href
  );
});

socket.on("createMessage", (message, userName) => {
  messages.innerHTML =
    messages.innerHTML +
    `<div class="message">
        <b><i class="far fa-user-circle"></i> <span> ${
          userName === user ? "me" : userName
        }</span> </b>
        <span>${message}</span>
    </div>`;
});