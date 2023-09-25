# Control-theory_Distributed-systems
The program allows to configure and compute discrete model of thermal processes in a rectangular object

Description:
This is the program that provides the ability to configure a rectangular object with its properties and compute thermal processes in it. 
The particular realisation aimed at finding the best temperatures of the defined heaters to match the required temps in the object. To solve this problem, the program executes calculations based on brute force method. 
The optimization of the compute process is performed according to the temperature increments at the monitored points. The result of the execution represents the best match and corresponding heaters temperatures that has been found according to the standard deviation

Stack: Java, Maven

## Math
Differential equation of thermal processes in 3D objects:\
![image](https://github.com/Angon-pro/Control-theory_Distributed-systems/assets/85078037/f519370f-e5c3-44ed-9a79-e2d870cd9e4f)

Coef "a" is the coefficient of thermal conductivity. In this work a = 8.5E-5 (value for aluminum)

Simplified form:\
![image](https://github.com/Angon-pro/Control-theory_Distributed-systems/assets/85078037/a89689a4-f77b-4dbe-946e-af5b9136a60c)

Discrete form using finite difference method:\
![image](https://github.com/Angon-pro/Control-theory_Distributed-systems/assets/85078037/51a732b5-54c4-46aa-a94d-8739f357b0dd)

Where:\
![image](https://github.com/Angon-pro/Control-theory_Distributed-systems/assets/85078037/eb2c1913-d3f9-4a51-8dd1-6b7b6424426c)
