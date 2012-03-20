function H = hess_himmelblau_func_1(x)
	H = [ 12*x(1)^2+4*x(2)-42    4*(x(1)+x(2));
             4*(x(1)+x(2))    4*x(1)+12*x(2)^2-26  ];
end