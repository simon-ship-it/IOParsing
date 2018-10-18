clear;clc;clf;
pause_time = 0.5;

dir = "square/";
dataset = "square_2";
m = load(dir + dataset + "/memristance.txt"); m = m';
figure(1); plot(m(:,2),m(:,1)); hold on;
pause(pause_time)

dataset = "square_4";
m = load(dir + dataset + "/memristance.txt"); m = m';
figure(1); plot(m(:,2),m(:,1)); hold on;
pause(pause_time)


dataset = "square_2_amp09";
m = load(dir + dataset + "/memristance.txt"); m = m';
figure(1); plot(m(:,2),m(:,1)); hold on;
pause(pause_time)


dir = "triangle/";
dataset = "triangle_2";
m = load(dir + dataset + "/memristance.txt"); m = m';
figure(1); plot(m(:,2),m(:,1));
pause(pause_time)



dataset = "triangle_4";
m = load(dir + dataset + "/memristance.txt"); m = m';
figure(1); plot(m(:,2),m(:,1));
pause(pause_time)


dataset = "triangle_8";
m = load(dir + dataset + "/memristance.txt"); m = m';
figure(1); plot(m(:,2),m(:,1));
pause(pause_time)


dataset = "triangle_16";
m = load(dir + dataset + "/memristance.txt"); m = m';
figure(1); plot(m(:,2),m(:,1));
pause(pause_time)




%% modified triangular wave generation
clear;clc;clf;
dt=0.01; T = 30;
t = 0:dt:T;
omega = 16;
amp = 1;
triangle = amp * sawtooth(t*omega,0.5);

% delay the signal
delay = 0.8 * 100;
triangle_delay = triangle(delay:length(triangle));
t_delay = t(1:length(t)-delay+1);

plot(t,triangle)

M = zeros(length(t),2);
M(:,2) = triangle;
M(:,1) = t;
dataName = "triangle_16";
dlmwrite("/users/simon/eclipse-workspace/simulator/src/data/par/triangle/" + dataName + "/" + dataName + ".txt",M, ' ')
disp('Done')
%% read triangle voltage data
d1 = load("/Users/simon/eclipse-workspace/simulator/src/data/triangle/modified/triangle_2/triangle_2.txt");
d2 = load("/Users/simon/eclipse-workspace/simulator/src/data/triangle/modified/triangle_4/triangle_4.txt");
d3 = load("/Users/simon/eclipse-workspace/simulator/src/data/triangle/modified/triangle_2_delay/triangle_2_delay.txt");
d4 = load("/Users/simon/eclipse-workspace/simulator/src/data/triangle/modified/triangle_4_delay/triangle_4_delay.txt");
plot(d1(:,2)); hold on;
plot(d2(:,2)); plot(d3(:,2)); plot(d4(:,2)); hold off;


%% modified square wave generator

clear;clc;clf;
dt=0.01; T = 30;
t = 0:dt:T;
omega = 2;
square = 0.9*square(t*omega);

% delay the signal
delay = 0.8 * 100;
square_delay = square(delay:length(square));
t_delay = t(1:length(t)-delay+1);

plot(t,square)
M = zeros(length(t),2);
M(:,2) = square;
M(:,1) = t;
dataName = "square_2_amp09";
dlmwrite("/users/simon/eclipse-workspace/simulator/src/data/par/square/" + dataName + "/" + dataName + ".txt",M, ' ')

%% plot

d = load("square/square_2/square_2.txt");
plot(d(:,2));