function test_holzmann_func_with_fmincon_too()
	lambda = 0.00001;
	a = [0.1; 0; 0];
	b = [100; 25.6; 5];
	x0 = [100; 12.5; 3];
	tol = 0.0001;
	itmax = 200;
	tic;
	[x_ssn,fval_ssn,it_ssn] = semismooth_newton('holzmann_func','grad_holzmann_func','hess_holzmann_func',lambda,a,b,x0,itmax,tol);
	t_ssn = toc;
	x1 = sprintf('%.3f ',x_ssn);
	f1 = sprintf('f(x_ssn) = %.3f',fval_ssn);
	t1 = sprintf('solved in %.2f ms.',t_ssn*1000);
	str1 = ['x_ssn = [ ', x1, '], ', f1, ', it = ', num2str(it_ssn), ', ', t1];
	A = [ -eye(length(a)); eye(length(b)) ];
	c = [ -a; b ];
	tic;
	[x_sqp,fval_sqp,it_sqp] = sqp('holzmann_func_v0','grad_holzmann_func_v0','hess_holzmann_func_v0',A,c,x0,itmax,tol);
	t_sqp = toc;
	x2 = sprintf('%.3f ',x_sqp);
	f2 = sprintf('f(x_sqp) = %.3f',fval_sqp);
	t2 = sprintf('solved in %.2f ms.',t_sqp*1000);
	str2 = ['x_sqp = [ ', x2, '], ', f2, ', it = ', num2str(it_sqp), ', ', t2];
	options = optimset('Algorithm','active-set','Display','off');
	tic;
	[x_fmc,fval_fmc,exitflag,output] = fmincon('holzmann_func_v0',x0,[],[],[],[],a,b,[],options);
	t_fmc = toc;
	x3 = sprintf('%.3f ',x_fmc);
	f3 = sprintf('f(x_fmc) = %.3f',fval_fmc);
	t3 = sprintf('solved in %.2f ms.',t_fmc*1000);
	str3 = ['x_fmc = [ ', x3, '], ', f3, ', ', t3];
	a = sprintf('%.3f ',a);
	b = sprintf('%.3f ',b);
	x0 = sprintf('%.3f ',x0);
	str0 = ['a = [ ', a, '], b = [ ', b, '], x0 = [ ', x0, ']'];
	disp(str0);
	disp(str1);
	disp(str2);
	disp(str3);
end