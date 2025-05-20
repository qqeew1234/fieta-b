@echo off
setlocal enabledelayedexpansion

:: Python 가상환경 설정 및 스크립트 실행 (Windows CMD 버전)

:: 로그 함수 정의
call :define_log_functions

:: 기본 설정값
set "VENV_DIR=.venv"
set "REQUIREMENTS_FILE=requirements.txt"
set "PYTHON_SCRIPT="

:: 명령행 인자 처리
:parse_args
if "%~1"=="" goto :check_args
if /i "%~1"=="-e" (
    set "VENV_DIR=%~2"
    shift
    shift
    goto :parse_args
) else if /i "%~1"=="--env" (
    set "VENV_DIR=%~2"
    shift
    shift
    goto :parse_args
) else if /i "%~1"=="-r" (
    set "REQUIREMENTS_FILE=%~2"
    shift
    shift
    goto :parse_args
) else if /i "%~1"=="--requirements" (
    set "REQUIREMENTS_FILE=%~2"
    shift
    shift
    goto :parse_args
) else if /i "%~1"=="-f" (
    set "PYTHON_SCRIPT=%~2"
    shift
    shift
    goto :parse_args
) else if /i "%~1"=="--file" (
    set "PYTHON_SCRIPT=%~2"
    shift
    shift
    goto :parse_args
) else if /i "%~1"=="-h" (
    call :show_help
    exit /b 0
) else if /i "%~1"=="--help" (
    call :show_help
    exit /b 0
) else (
    call :log_error "알 수 없는 옵션: %~1"
    call :show_help
    exit /b 1
)

:check_args
:: 필수 인자 검증
if "%PYTHON_SCRIPT%"=="" (
    call :log_error "실행할 파이썬 파일을 지정해야 합니다. (-f 옵션 사용)"
    call :show_help
    exit /b 1
)

:: Python 버전 확인
where python >nul 2>nul
if %ERRORLEVEL% neq 0 (
    call :log_error "Python이 설치되어 있지 않습니다."
    exit /b 1
)

for /f "tokens=*" %%i in ('python --version 2^>^&1') do (
    call :log_info "Python 버전: %%i"
)

:: 1. 가상 환경 생성 및 활성화
if not exist "%VENV_DIR%" (
    call :log_info "가상 환경 생성 중: %VENV_DIR%"
    python -m venv "%VENV_DIR%"
    if %ERRORLEVEL% neq 0 (
        call :log_error "가상 환경 생성 실패"
        exit /b 1
    )
) else (
    call :log_info "기존 가상 환경 사용: %VENV_DIR%"
)

set "ACTIVATE_SCRIPT=%VENV_DIR%\Scripts\activate.bat"
if not exist "%ACTIVATE_SCRIPT%" (
    call :log_error "활성화 스크립트를 찾을 수 없습니다: %ACTIVATE_SCRIPT%"
    exit /b 1
)

call :log_info "가상 환경 활성화 중"
call "%ACTIVATE_SCRIPT%"

:: 2. 필요한 모듈 설치
if exist "%REQUIREMENTS_FILE%" (
    call :log_info "필요한 패키지 설치 중 (from %REQUIREMENTS_FILE%)"
    pip install -r "%REQUIREMENTS_FILE%"
    if %ERRORLEVEL% neq 0 (
        call :log_error "패키지 설치 실패"
        exit /b 1
    )
) else (
    call :log_warning "요구사항 파일을 찾을 수 없습니다: %REQUIREMENTS_FILE%. 패키지 설치를 건너뜁니다."
)

:: 3. 파이썬 파일 실행
if exist "%PYTHON_SCRIPT%" (
    call :log_info "파이썬 스크립트 실행 중: %PYTHON_SCRIPT%"
    python "%PYTHON_SCRIPT%"
    set EXIT_CODE=%ERRORLEVEL%

    if !EXIT_CODE! neq 0 (
        call :log_error "파이썬 스크립트 실행 실패 (종료 코드: !EXIT_CODE!)"
        exit /b !EXIT_CODE!
    )

    call :log_info "스크립트 실행 완료"
) else (
    call :log_error "파이썬 파일을 찾을 수 없습니다: %PYTHON_SCRIPT%"
    exit /b 1
)

:: 가상 환경 비활성화
call :log_info "가상 환경 비활성화 완료"
deactivate
exit /b 0

:: 함수 정의
:show_help
echo 사용법: %~nx0 [옵션]
echo.
echo 옵션:
echo   -e, --env ENV_NAME      가상환경 디렉토리 이름 (기본값: .venv)
echo   -r, --requirements FILE 요구사항 파일 경로 (기본값: requirements.txt)
echo   -f, --file FILE         실행할 파이썬 파일
echo   -h, --help              도움말 표시
echo.
echo 예시:
echo   %~nx0 -f app.py
echo   %~nx0 -e custom_env -r custom_requirements.txt -f app.py
exit /b 0

:define_log_functions
:: 로그 함수 정의
goto :EOF

:log_info
echo [INFO] %~1
exit /b 0

:log_warning
echo [WARNING] %~1
exit /b 0

:log_error
echo [ERROR] %~1 1>&2
exit /b 0