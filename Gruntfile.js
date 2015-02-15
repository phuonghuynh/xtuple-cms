module.exports = function (grunt) {
  grunt.initConfig({
    pkg: grunt.file.readJSON("package.json"),

    clean: {
      build: ["<%=pkg.public%>", "<%=pkg.assets%>css", "<%=pkg.assets%>bower_components", "<%=pkg.assets%>index.html"]
    },

    copy: {
      build: {
        files: [
          {
            cwd: "<%=pkg.assets%>",
            expand: true,
            src: ["**"],
            dest: "<%=pkg.public%>"
          }
        ]
      },
      run: {
        files: [
          {
            cwd: "<%=pkg.public%>",
            expand: true,
            src: ["bower_components/**", "css/**", "index.html"],
            dest: "<%=pkg.assets%>"
          }
        ]
      }
    },

    "bower-install-simple": {
      options: {
        directory: "<%=pkg.public%>bower_components"
      },
      build: {
        options: {
          production: true,
          forceLatest: true
        }
      }
    },

    wiredep: {
      options: {
        color: true,
        directory: "<%=pkg.public%>bower_components",
        fileTypes: {
          html: {
            block: /(([ \t]*)<!--\s*bower:*(\S*)\s*-->)(\n|\r|.)*?(<!--\s*endbower\s*-->)/gi,
            detect: {
              js: /<script.*src=['"]([^'"]+)/gi,
              css: /<link.*href=['"]([^'"]+)/gi
            },
            replace: {
              js: '<script src="{{filePath}}" charset="utf-8"></script>',
              css: '<link rel="stylesheet" href="{{filePath}}" />'
            }
          }
        }
      },
      target: {
        src: ["<%=pkg.public%>index.html"]
      }
    },

    includeSource: {
      options: {
        basePath: "<%=pkg.public%>",
        duplicates: false,
        debug: true
      },
      target: {
        files: {
          "<%=pkg.public%>index.html": "<%=pkg.public%>index.tem.html"
        }
      }
    },

    watch: {
      scripts: {
        files: ["*.js", "*.json"],
        options: {
          livereload: true
        }
      },
      markup: {
        files: ["*.html"],
        options: {
          livereload: true
        }
      },
      stylesheets: {
        files: ["*.css"],
        options: {
          livereload: true
        }
      }
    },

    connect: {
      server: {
        options: {
          port: 8080,
          base: "src/main/webapp/assets",
          keepalive: true
        }
      }
    },

    ngAnnotate: {
      main: {
        files: [{
          cwd: "<%=pkg.public%>",
          expand: true,
          ext: "css",
          src: ['modules/**/*.js'],
          dest: "<%=pkg.public%>"
        }]
      }
    },

    sass: {
      options: {
        sourceMap: false,
        imagePath: "../images"
      },
      dist: {
        files: [{
          cwd: "<%=pkg.public%>/scss",
          expand: true,
          src: ['*.scss'],
          ext: ".css",
          dest: "<%=pkg.public%>/css"
        }]
      }
    }
  });

  grunt.loadNpmTasks("grunt-contrib-watch");
  grunt.loadNpmTasks("grunt-contrib-connect");
  grunt.loadNpmTasks("grunt-include-source");
  grunt.loadNpmTasks("grunt-wiredep");
  grunt.loadNpmTasks("grunt-bower-install-simple");
  grunt.loadNpmTasks("grunt-contrib-copy");
  grunt.loadNpmTasks("grunt-contrib-clean");
  grunt.loadNpmTasks("grunt-ng-annotate");
  grunt.loadNpmTasks("grunt-sass");

  grunt.registerTask("build", [
    "clean:build",
    "copy:build",
    "sass",
    "bower-install-simple:build",
    "includeSource:target",
    "wiredep:target",
    "ngAnnotate:main"
  ]);

  grunt.registerTask("jetty", ["build", "copy:run"]);
  grunt.registerTask("run", ["build", "copy:run", "connect", "watch"]);
};