%problem_Gv_example_16_4_nocedal_wright
%(Vgl. Beispiel 16.4 in \cite[S.~475]{nocedal})
%\min_{x\in\R^2}\ (x_1 - 1)^2 + (x_2 - 2.5)^2
%\nb -x_1 + 2 x_2 & \leq 2 \\
%x_1 + 2 x_2 & \leq 6 \\
%x_1 - 2 x_2 & \leq 2 \\
%0 & \leq x_i,\ i = 1,2 \\
%
function y = func_for_problem_Gv_example_16_4_nocedal_wright(x)
	xd = [1; 2.5];
	y = norm(x-xd)^2;
end