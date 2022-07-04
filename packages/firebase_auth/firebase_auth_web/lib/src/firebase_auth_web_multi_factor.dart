// ignore_for_file: require_trailing_commas
// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:firebase_auth_platform_interface/firebase_auth_platform_interface.dart';
import 'package:firebase_core_web/firebase_core_web_interop.dart'
    as core_interop;

import 'interop/auth.dart' as auth_interop;

/// Web delegate implementation of [UserPlatform].
class MultiFactorWeb extends MultiFactorPlatform {
  MultiFactorWeb(FirebaseAuthPlatform auth) : super(auth);

  /// instance of Auth from the web plugin
  auth_interop.Auth? _webAuth;

  auth_interop.Auth get _delegate {
    return _webAuth ??=
        auth_interop.getAuthInstance(core_interop.app(auth.app.name));
  }
}
