package com.example.ecommerce_system.util;

import java.util.List;

public record SqlAndParams(String sql, List<Object> params) {}
