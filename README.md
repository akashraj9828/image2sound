# image2sound

  
## TL;DR
---
>Converts an image to sound. Image can be seen in spectogram of generated sound. 

>Applications: Steganography, Encryption, Security, Communication
  
## Full Explanation
---
>Large demand of various applications requires data to be transmitted in such a manner, that it should remain secure. Data transmission in public communication system is not secure because of interception and Interruption by hackers. Steganography is a method of hiding secret data, by embedding it into an audio, video, image or text file in such a way that it is neither detectable easily nor easily extractable. It is one of the methods employed to protect secret or sensitive data from malicious attacks.

>The Idea of this program is to achieve Steganography by hiding image data inside an audio and ensure secure transfer of data. The images are first converted into audio and that audio is superimposed over another carrier audio that might be song or a voice recording. The original image cannot be extracted without knowing the original carrier audio wave which makes the transfer secure and the presence of hidden data is not detectable to a normal user in any way when listening to the modified audio.

## Requitement
``` 
- Java 8
- Audio analyzer with spectogram feature (Sonic visualizer for Windows included)
```
## How to use
``` 
> javac img2sound.java
> java  img2sound [png image name without .png] [Output file length in senonds]
```

[elephant](/out.wav)

<audio controls=true src="/out.wav"/>
