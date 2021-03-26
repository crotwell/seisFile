class Seisfile < Formula
  desc "library for reading and writing seismic file formats in java"
  homepage "https://www.seis.sc.edu/seisFile.html"
  url "https://www.seis.sc.edu/downloads/seisFile/prerelease/seisFile-2.0.1-SNAPSHOT.tgz"
  sha256 "a5f6d21ac54897479fdb40cf91a0f98a2fff35faab2cb8bcbab3e3e501a0b581"
  license "LGPL-3.0-or-later"

  bottle :unneeded

  def install
    rm_f Dir["bin/*.bat"]
    man1.install "docs/manpage/seisfile.1"
    etc.install Dir["docs/bash_completion.d"]
    libexec.install %w[bin lib]
    env = if Hardware::CPU.arm?
      Language::Java.overridable_java_home_env("11")
    else
      Language::Java.overridable_java_home_env
    end
    (bin/"seisfile").write_env_script libexec/"bin/seisfile", env
  end

  test do
    assert_match version.to_s, shell_output("#{bin}/seisfile --version")
    help_output = shell_output("#{bin}/seisfile help")
    assert_includes help_output, "Usage: seisfile [COMMAND]"
  end
end
