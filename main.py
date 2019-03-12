import os
import re
import shutil
import json
import time

LOG = ''

# -----------------------------
# 递归目录地复制指定文件
# 相应的源码路径会替换成编译路径
# -----------------------------
def copy_file_recursive(config):
    global LOG
    work_paths = config['workPaths']
    source_path = config['sourcePaths']
    output_path = config['outputPath']
    target_path = config['targetPath']
    home_path = config['homePath']

    for path in work_paths:
        final_path = path
        source_match = is_arr_match_str(source_path, path)
        if (source_match is not None):
            # 源码路径替换成编译路径
            final_path = path.replace(source_match, output_path, 1)
            # 文件扩展名替换
            final_path = final_path.replace(".java", ".class")
        final_abs_path = path_join(target_path, get_file_path(final_path))
        # 创建不存在的目录
        if not os.path.exists(final_abs_path):
            os.makedirs(final_abs_path)
        # 复制文件
        source_filename = path_join(home_path, final_path)
        try:
            shutil.copy(source_filename, final_abs_path)
        except FileNotFoundError:
            # print("未找到文件: " + source_filename)
            LOG += "未找到文件: " + source_filename + "\n"
        else:
            # print("已复制：'" + source_filename + "'\n    至：'" + final_abs_path + "'")
            LOG += "已复制：'" + source_filename + "'\n    至：'" + final_abs_path + "'\n"
        # print("-------------------------------------")
        LOG += "-------------------------------------\n"

    # -----------------------------
# 数组arr中的元素在str中吗？
# 在，就返回这个元素；不在，就返回空
# -----------------------------
def is_arr_match_str(arr, str):
    for a in arr:
        if (re.search(a, str) is not None): return a
    return None

# -----------------------------
# 去掉文件路径中的文件名
# "D:/test/test.py" -> "D:/test/"
# -----------------------------
def get_file_path(path):
    (filePath, filename) = os.path.split(path)
    return filePath

# -----------------------------
# 载入配置文件
# -----------------------------
def load_config(path):
    f = open(path, encoding="utf-8")
    try:
        config = json.load(f)
    except json.decoder.JSONDecodeError:
        return None
    return config

# -----------------------------
# 生成目录树字符串
# 源自：https://www.polarxiong.com/archives/Python-%E7%94%9F%E6%88%90%E7%9B%AE%E5%BD%95%E6%A0%91.html
# -----------------------------
def get_dir_list(path, placeholder=''):
    BRANCH = '├─'
    LAST_BRANCH = '└─'
    TAB = '│  '
    EMPTY_TAB = '   '
    folder_list = [folder for folder in os.listdir(path) if os.path.isdir(os.path.join(path, folder))]
    file_list = [file for file in os.listdir(path) if os.path.isfile(os.path.join(path, file))]
    result = ''
    for folder in folder_list[:-1]:
        result += placeholder + BRANCH + folder + '\n'
        result += get_dir_list(os.path.join(path, folder), placeholder + TAB)
    if folder_list:
        result += placeholder + (BRANCH if file_list else LAST_BRANCH) + folder_list[-1] + '\n'
        result += get_dir_list(os.path.join(path, folder_list[-1]), placeholder + (TAB if file_list else EMPTY_TAB))
    for file in file_list[:-1]:
        result += placeholder + BRANCH + file + '\n'
    if file_list:
        result += placeholder + LAST_BRANCH + file_list[-1] + '\n'
    return result

# -----------------------------
# 得到格式化时间戳
# -----------------------------
def get_time_format():
    return time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())

# -----------------------------
# 得到格式化日期
# -----------------------------
def get_date_format():
    return time.strftime("%Y-%m-%d", time.localtime())

def path_join(a, b):
    if b.startswith("/"):
        b = b.replace("/", "", 1)
    return os.path.join(a, b)

def save_log(txt, path='./'):
    f = open(path + get_date_format() + '.txt', 'a')
    f.write(txt)
    f.close()

def main():
    global LOG
    LOG += "\n======== " + get_time_format() + " 给我生 <(￣︶￣)↗[GO!] ========\n"
    config = load_config("config.json")
    if config is None:
        LOG += "error: 解析配置文件出错 (ノへ￣、)\n"
        LOG += "======== " + get_time_format() + " 生...生不出来惹 _(:з)∠)_ ========\n"
        return
    copy_file_recursive(config)
    dirtree = get_dir_list(config['targetPath'])
    LOG += "最后生成的文件目录如下：\n"
    LOG += dirtree + "\n"
    LOG += "======== " + get_time_format() + " 哦呼，擦汗 ^o^y ========\n"
    if config['log']:
        save_log(LOG, config['logPath'])
        LOG += "日志已保存"
    print(LOG)
    input("Press <Enter> to Esc")

if __name__ == '__main__':
    main()