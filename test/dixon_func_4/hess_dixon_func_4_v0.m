function H = hess_dixon_func_4_v0(x)
	lambda = 0.0000000001;
	H = hess_dixon_func_4(x) + lambda*eye(length(x));
end