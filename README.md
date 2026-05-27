# Net Widget

A home screen Android widget to track and monitor your remaining mobile internet quota.

## Supported Operators

Currently, it only supports **Shatel Mobile**.

## Supported Android Version

Android 12 or above

_note_: Due to a lack of physical test devices and the current internet blockage in Iran—which
prevents the download of large virtual device images—I am unable to test or adapt the app for
versions below Android 12. If you have access to an older device and can verify a build, I would be
happy to review and merge your Pull Request.

## Known Issues

* **Per App Language:** Widget's text might not have proper padding when the per app language is set
  to a locale with a different direction of the system locale. For exmaple if the system is set to
  `English` which is `LTR` and the app is set to `Farsi` which is `RTL`. This issue is due to
  inconsistent rtl application of glance widget api.

## TODO

- [x] Open app on tap on widget
- [x] Neutral color scheme compatibility
    * The One UI is the issue

- [ ] Google password manager select password cause crash
- [x] Login states are lost on configuration change
- [x] Settings for widget
- [x] Traffic amount version of widget
- [x] Widget preview
- [x] App logo
- [ ] Complete the readme file
- [x] Add full Farsi translations
- [x] Rename the project