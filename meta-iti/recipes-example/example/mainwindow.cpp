#include "mainwindow.h"
#include "ui_mainwindow.h"

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);
}

MainWindow::~MainWindow()
{
    delete ui;
}


void MainWindow::on_onButton_clicked()
{
    system("echo 2 > /sys/class/gpio/export");
    system("echo out > /sys/class/gpio/gpio2/direction");
    system("echo 1 > /sys/class/gpio/gpio2/value");
}


void MainWindow::on_offButton_clicked()
{
    system("echo 0 > /sys/class/gpio/gpio17/value");
}

