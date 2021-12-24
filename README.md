# SignalScroll
JavaFX application that communicates with alarm monitoring automation platform API to output a live view of all alarm signals currently being received. Uses Java, JavaFX, XML, JSON and CSS.
<br><br>
Login Screen:<br>
![login](https://user-images.githubusercontent.com/68821944/147366414-f3517f1c-6cd3-4b96-a476-41fadaab70c3.png)
<br><br>
Upon successful login, you have the option to choose a specific alarm receiver task and/or filter the live events.  You can leave these blank and hit the Start button to start the live event viewer:
![start](https://user-images.githubusercontent.com/68821944/147366815-9fcc93c4-f319-43fa-a852-df98ea9a32ee.png)
<br><br>
The application communicates with the automation alarm receiver platform's API every 3 seconds to check for new activity.<br>
If new activity is received, the application outputs to the live signal scroll putting most recent activity at the bottom.<br>
Higher priority alarm events are colorized so they stand out to the user.<br>
Once the events reach 500, the oldest items are purged to make room for newer events.<br>
![scrolling](https://user-images.githubusercontent.com/68821944/147366816-e0aa8ccb-43ac-4c96-87da-13c5e96e3b84.png)
<br>
<i>Image contains customer's private information so the contents have been blurred.</i>
